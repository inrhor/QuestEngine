package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.quest.module.inner.QuestInnerModule
import cn.inrhor.questengine.api.quest.module.inner.QuestTarget
import cn.inrhor.questengine.api.quest.module.main.QuestMode
import cn.inrhor.questengine.api.quest.module.main.QuestModule
import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.ControlData
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.PlayerData
import cn.inrhor.questengine.common.database.data.quest.*
import cn.inrhor.questengine.common.database.data.teamData
import cn.inrhor.questengine.common.quest.ModeType
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestModule
import cn.inrhor.questengine.common.quest.ui.QuestBookBuildManager
import cn.inrhor.questengine.script.kether.runEval
import cn.inrhor.questengine.script.kether.runEvalSet

import cn.inrhor.questengine.utlis.file.FileUtil
import cn.inrhor.questengine.utlis.newLineList
import cn.inrhor.questengine.utlis.time.*
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.io.deepDelete
import taboolib.common.io.newFile
import taboolib.common.platform.function.*
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Configuration.Companion.setObject
import taboolib.platform.util.sendLang
import java.io.File
import java.util.*

object QuestManager {

    /**
     * 注册的任务模块内容
     */
    var questMap = mutableMapOf<String, QuestModule>()

    /**
     * 自动接受的任务模块内容
     */
    var autoQuestMap = mutableMapOf<String, QuestModule>()

    /**
     * 注册任务模块内容
     */
    fun register(questID: String, questModule: QuestModule, sort: String = "") {
        questMap[questID] = questModule
        questModule.innerQuestList.forEach {
            it.target.forEach { e->
                e.loadNode()
            }
        }
        if (questModule.accept.way.lowercase() == "auto") {
            autoQuestMap[questID] = questModule
        }
        QuestBookBuildManager.addSortQuest(sort, questModule)
    }

    /**
     * 得到任务模块内容
     */
    fun getQuestModule(questID: String): QuestModule? {
        return questMap[questID]
    }

    /**
     * 得到内部任务模块内容
     */
    fun getInnerQuestModule(questID: String, innerQuestID: String): QuestInnerModule? {
        val questModule = questMap[questID]?: return null
        questModule.innerQuestList.forEach {
            if (it.id == innerQuestID) return it
        }
        return null
    }

    fun UUID.getQuestID(player: Player): String {
        return getQuestData(player, this)?.questID?: ""
    }

    fun UUID.getQuestModule(player: Player): QuestModule? {
        return QuestManager.getQuestModule(this.getQuestID(player))
    }

    /**
     * @return 是否满足任务成员模式
     */
    fun String.matchQuestMode(player: Player, share: Boolean = true): Boolean {
        val questModule = getQuestModule(this)?: return false
        return questModule.matchQuestMode(player, share)
    }

    /**
     * @return 是否满足任务成员模式
     */
    fun UUID.matchQuestMode(player: Player, share: Boolean = true): Boolean {
        val data = getQuestData(player, this)?: return false
        val questModule = getQuestModule(data.questID)?: return false
        return questModule.matchQuestMode(player, share)
    }

    fun QuestModule.matchQuestMode(player: Player, share: Boolean = true): Boolean {
        val mode = this.mode
        if (mode.type == ModeType.PERSONAL) return true
        val amount = mode.amount
        if (amount <= 1) return true
        val tData = player.teamData()?: return false
        if (amount >= TeamManager.getMemberAmount(tData)) {
            if (share) {
                if (mode.shareData) return true
            }else return true
        }
        return false
    }

    /**
     * 获取任务成员模式
     */
    fun getQuestMode(questID: String): ModeType {
        val questModule = getQuestModule(questID)?: return ModeType.PERSONAL
        return questModule.mode.type
    }

    /**
     * @param questUUID
     * @return 是否存在任务数据
     */
    fun existQuestData(player: Player, questUUID: UUID): Boolean {
        val pData = DataStorage.getPlayerData(player)
        pData.questDataList.values.forEach {
            if (it.questUUID == questUUID) return true
        }
        return false
    }

    /**
     * 任务数据中是否存在 QuestID
     */
    fun existQuestData(uuid: UUID, questID: String): Boolean {
        val pData = DataStorage.getPlayerData(uuid)
        pData.questDataList.values.forEach {
            if (it.questID == questID) return true
        }
        return false
    }

    /**
     * 任务数据中是否存在 QuestID 及其状态
     */
    fun existQuestData(uuid: UUID, questID: String, state: QuestState): Boolean {
        val pData = DataStorage.getPlayerData(uuid)
        pData.questDataList.values.forEach {
            if (it.questID == questID && it.state == state) return true
        }
        return false
    }

    /**
     * 任务数据中 QuestID 存在数量
     */
    fun questDataAmount(uuid: UUID, questID: String): Int {
        var amount = 0
        val pData = DataStorage.getPlayerData(uuid)
        pData.questDataList.values.forEach {
            if (it.questID == questID) amount++
        }
        return amount
    }

    /**
     * 接受任务
     */
    fun acceptQuest(player: Player, questID: String) {
        val pData = DataStorage.getPlayerData(player)
        val questModule = getQuestModule(questID) ?: return
        if (questModule.mode.type == ModeType.COLLABORATION) {
            val tData = pData.teamData ?: return
            if (!acceptCondition(tData.playerMembers(), questID)) return
                tData.members.forEach {
                val m = Bukkit.getPlayer(it) ?: return@forEach
                acceptQuest(m, questModule)
            }
            return
        }
        if (!acceptCondition(mutableSetOf(player), questID)) return
        acceptQuest(player, questModule)
    }

    private fun acceptQuest(player: Player, questModule: QuestModule) {
        val startInnerQuest = questModule.getStartInnerQuest()?: return
        val questUUID = UUID.randomUUID()
        checkTimeTask(player, questUUID, questModule)
        acceptInnerQuest(player, questUUID, questModule.questID, startInnerQuest, true)
    }

    /**
     * @return 任务可许人数
     */
    fun passMaxQuantity(players: MutableSet<Player>, questModule: QuestModule): Boolean {
        val max = questModule.accept.maxQuantity
        if (max < 0) return true
        players.forEach {
            if (questDataAmount(it.uniqueId, questModule.questID) >= max) return false
        }
        return true
    }

    /**
     * 接受任务检查是否通过条件
     */
    fun acceptCondition(players: MutableSet<Player>, questID: String): Boolean {
        val questModule = getQuestModule(questID) ?: return false
        if (!passMaxQuantity(players, questModule)) return false
        val accept = questModule.accept
        val check = accept.check
        val condition = accept.condition
        val c = condition.newLineList()
        if (c.isEmpty()) return true
        if (check <= 0) {
            return runEvalSet(players, condition)
        }
        val list = mutableListOf<String>()
        var i = 0
        c.forEach {
            list.add(it)
            i++
            if (i >= check) return@forEach
        }
        return runEvalSet(players, list.joinToString(" "))
    }

    /**
     * 接受任务后开始使用调度器检查条件，一旦不符合将失败
     * 同时启用任务目标完成的检查
     *
     * 属于自动化模块
     */
    fun checkTimeTask(player: Player, questUUID: UUID, questID: String) {
        val questModule = getQuestModule(questID)?: return
        checkTimeTask(player, questUUID, questModule)
    }

    /**
     * 接受任务后开始使用调度器检查条件，一旦不符合将失败
     * 同时启用任务目标完成的检查
     *
     * 属于自动化模块
     */
    fun checkTimeTask(player: Player, questUUID: UUID, questModule: QuestModule) {
        val list = mutableListOf<String>()
        val fail = questModule.failure
        val check = fail.check
        val condition = fail.condition
        val c = condition.newLineList()
        var i = 0
        val questData = getQuestData(player, questUUID)?: return
        val innerData = questData.questInnerData
        val targets = innerData.targetsData
        val targetSize = targets.size
        if (targetSize > 0) {
            submit(async = true, period = 10L) {
                if (!player.isOnline || innerData.state != QuestState.DOING) {
                    cancel(); return@submit
                }
                if (innerData.isFinishTarget()) {
                    cancel()
                    finishInnerQuest(player, questData, innerData)
                    return@submit
                }
            }
        }
        if (c.isEmpty()) return
        c.forEach {
            list.add(it)
            i++
            if (i >= check) return@forEach
        }
        submit(async = true, period = 10L) {
            if (!player.isOnline) {
                cancel(); return@submit
            }
            val qData = getQuestData(player, questUUID)
            if (qData == null || qData.state != QuestState.DOING || qData.questInnerData.state != QuestState.DOING) {
                cancel(); return@submit
            }
            if (!runEval(player, list)) {
                val modeType = questModule.mode.type
                endQuest(player, modeType, questUUID, QuestState.FAILURE, false)
                runFailTime(player, modeType, questModule.failure.script)
                cancel()
                return@submit
            }
        }
    }

    private fun runFailTime(player: Player, modeType: ModeType, failKether: String) {
        val pData = DataStorage.getPlayerData(player)
        val tData = pData.teamData
        if (modeType == ModeType.COLLABORATION && tData != null) {
            tData.playerMembers().forEach {
                runEval(it, failKether)
            }
            return
        }
        runEval(player, failKether)
    }

    /**
     * 接受下一个内部任务
     *
     * 前提是已接受任务
     */
    private fun acceptNextInner(player: Player, questData: QuestData, innerQuestID: String) {
        val questID = questData.questID
        val questUUID = questData.questUUID
        val questModule = getQuestModule(questID) ?: return
        if (questModule.mode.type == ModeType.COLLABORATION) {
            val tData = player.teamData()?: return
            tData.members.forEach {
                val m = Bukkit.getPlayer(it)?: return@forEach
                nextInnerQuest(m, getQuestData(m, questUUID)!!, innerQuestID)
            }
            return
        }
        nextInnerQuest(player, questData, innerQuestID)
    }

    private fun nextInnerQuest(player: Player, questData: QuestData, nextInnerID: String) {
        val questID = questData.questID
        val questInnerModule = getInnerQuestModule(questID, nextInnerID) ?: return
        acceptInnerQuest(player, questData.questUUID, questID, questInnerModule, false)
    }

    /**
     * 接受内部任务
     */
    fun acceptInnerQuest(player: Player, questData: QuestData, innerQuestID: String, isNewQuest: Boolean) {
        val questID = questData.questID
        val innerModule = getInnerQuestModule(questID, innerQuestID) ?: return
        acceptInnerQuest(player, questData.questUUID, questID, innerModule, isNewQuest)
    }

    fun acceptInnerQuest(player: Player, questID: String, innerQuestID: String, isNewQuest: Boolean) {
        val questData = getQuestData(player.uniqueId, questID) ?: return
        val innerModule = getInnerQuestModule(questID, innerQuestID) ?: return
        acceptInnerQuest(player, questData.questUUID, questID, innerModule, isNewQuest)
    }

    private fun acceptInnerQuest(player: Player, questUUID: UUID, questID: String, innerQuestModule: QuestInnerModule, isNewQuest: Boolean) {
        val pData = DataStorage.getPlayerData(player)
        val state = QuestState.DOING
        val innerQuestID = innerQuestModule.id
        val innerModule = getInnerQuestModule(questID, innerQuestID)?: return
        val questModule = getQuestModule(questID)?: return
        val targetDataMap = mutableMapOf<String, TargetData>()
        val innerQuestData = QuestInnerData(questID, innerQuestID, targetDataMap, state)
        val questData = if (existQuestData(player, questUUID)) getQuestData(player, questUUID) !!
        else QuestData(questUUID, questID, innerQuestData, state)
        if (existQuestData(player, questUUID)) {
            questData.questInnerData = innerQuestData
            questData.state = state
        }
        innerModule.target.forEach {
            val targetData = TargetData(questUUID, innerQuestID, it.name, 0, it)
            targetDataMap[it.id] = targetData
            if (it.name.lowercase().startsWith("task ")) {
                targetData.runTask(player, questData, innerQuestData,questModule.mode.type)
            }
        }
        innerQuestData.targetsData = targetDataMap
        pData.questDataList[questUUID] = questData
        innerModule.timeAccept(player, innerModule,innerQuestData)
        ControlManager.saveControl(player, pData, innerQuestData)
        if (isNewQuest) {
            Database.database.createQuest(player, questUUID, questData)
        }
    }

    /**
     * 设置任务状态，包括内部任务
     */
    fun setQuestState(player: Player, questData: QuestData, state: QuestState) {
        if (state == QuestState.DOING) {
            val pData = DataStorage.getPlayerData(player)
            pData.questDataList.values.forEach {
                if (it.state == state) setQuestState(player, it, state)
            }
        }
        questData.state = state
        questData.questInnerData.state = state
        if (state == QuestState.DOING) {
            checkTimeTask(player, questData.questUUID, questData.questID)
        }
    }

    /**
     * 设置任务状态，包括内部任务
     */
    fun setQuestState(player: Player, questID: String, state: QuestState) {
        val questData = getQuestData(player.uniqueId, questID)?: return
        setQuestState(player, questData, state)
    }

    /**
     * 检索是否已有处于 DOING 状态的内部任务
     */
    fun hasDoingInnerQuest(pData: PlayerData): Boolean {
        return hasStateInnerQuest(pData, QuestState.DOING)
    }

    /**
     * 检索是否已有处于某状态的内部任务
     */
    fun hasStateInnerQuest(pData: PlayerData, state: QuestState): Boolean {
        val questData = pData.questDataList
        questData.values.forEach {
            if (it.questInnerData.state == state) return true
        }
        return false
    }

    /**
     * 检索是否已有处于某状态的内部任务
     */
    fun hasStateInnerQuest(player: Player, state: QuestState): Boolean {
        return hasStateInnerQuest(DataStorage.getPlayerData(player), state)
    }

    /**
     * 查看内部任务的状态是否为所求
     */
    fun isStateInnerQuest(player: Player, questUUID: UUID, state: QuestState): Boolean {
        val qData = getQuestData(player, questUUID)?: return false
        return qData.questInnerData.state == state
    }

    /**
     * 结束任务，最终结束
     * 成功脚本在目标完成时运行
     *
     * @param state 设定任务成功与否
     * @param innerFailReward 如果失败，是否执行当前内部任务失败脚本
     */
    private fun endQuest(player: Player, questData: QuestData, state: QuestState, innerFailReward: Boolean) {
        questData.state = state
        val innerData = questData.questInnerData
        innerData.state = state
        if (state == QuestState.FAILURE && innerFailReward) {
            val innerQuestID = innerData.innerQuestID
            val innerModule = getInnerQuestModule(questData.questID, innerQuestID)
            if (innerModule != null) {
                runEval(player, innerModule.fail)
            }
        }
    }

    /**
     * 结束任务，最终结束
     * 成功脚本在目标完成时运行
     *
     * @param state 设定任务成功与否
     * @param innerFailReward 如果失败，是否执行当前内部任务失败脚本
     */
    fun endQuest(player: Player, modeType: ModeType, questUUID: UUID, state: QuestState, innerFailReward: Boolean) {
        val pData = DataStorage.getPlayerData(player)
        val tData = pData.teamData
        if (modeType == ModeType.COLLABORATION && tData != null) {
            tData.playerMembers().forEach {
                val qData = getQuestData(player, questUUID)
                if (qData != null) {
                    endQuest(it, qData, state, innerFailReward)
                }
            }
            return
        }
        val qData = getQuestData(player, questUUID)?: return
        endQuest(player, qData, state, innerFailReward)
    }

    fun endQuest(player: Player, questID: String, state: QuestState, innerFailReward: Boolean) {
        val questModule = getQuestModule(questID)
        val questData = getQuestData(player.uniqueId, questID)

        if (questModule == null || questData == null) {
            player.sendLang("QUEST-NULL_QUEST_DATA", questID)
            return
        }

        endQuest(player, questModule.mode.type, questData.questUUID, state, innerFailReward)
    }

    fun finishInnerQuest(player: Player, questData: QuestData, questInnerData: QuestInnerData) {
        finishInnerQuest(player, questData.questUUID, questData.questID, questInnerData.innerQuestID)
    }

    /**
     * 完成内部任务
     */
    fun finishInnerQuest(player: Player, questUUID: UUID, questID: String, innerQuestID: String) {
        val questData = getQuestData(player, questUUID) ?: return
        val innerData = getInnerQuestData(player, questUUID, innerQuestID)
        val questModule = getQuestModule(questID)?: return
        innerData?.stateToggle(player, questData, QuestState.FINISH, questModule, reward = true, isTrigger = true)
    }

    fun finishInnerQuest(player: Player, questID: String, innerQuestID: String) {
        val questData = getQuestData(player.uniqueId, questID) ?: return
        finishInnerQuest(player, questData.questUUID, questID, innerQuestID)
    }

    /**
     * 精准检索玩家任务数据
     */
    fun getQuestData(player: Player, questUUID: UUID): QuestData? {
        val pData = DataStorage.getPlayerData(player)
        return pData.questDataList[questUUID]
    }

    /**
     * 根据 QuestID 模糊检索玩家任务数据
     */
    fun getQuestData(uuid: UUID, questID: String): QuestData? {
        val pData = DataStorage.getPlayerData(uuid)
        pData.questDataList.values.forEach {
            if (it.questID == questID) return it
        }
        return null
    }

    /**
     * @return 获得玩家当前内部任务数据
     */
    fun getInnerQuestData(player: Player, questUUID: UUID): QuestInnerData? {
        val questData = getQuestData(player, questUUID) ?: return null
        return questData.questInnerData
    }

    /**
     * @return 获得指定玩家内部任务数据
     */
    fun getInnerQuestData(player: Player, questUUID: UUID, innerQuestID: String): QuestInnerData? {
        return Database.database.getInnerQuestData(player, questUUID, innerQuestID)
    }

    /**
     * 得到内部任务内容的任务目标，交给数据
     *
     * 此为初始值，可许更新
     */
    fun getInnerModuleTargetMap(questUUID: UUID, innerModule: QuestInnerModule): MutableMap<String, TargetData> {
        val targetDataMap = mutableMapOf<String, TargetData>()
        innerModule.target.forEach {
            val targetData = TargetData(questUUID, innerModule.id, it.name, 0, it)
            targetDataMap[it.id] = targetData
        }
        return targetDataMap
    }

    /**
     * @return 获得正在进行中的任务集合
     */
    fun getDoingQuest(player: Player, checkMode: Boolean = true): List<QuestData> {
        val list = mutableListOf<QuestData>()
        val pData = DataStorage.getPlayerData(player)
        if (pData.questDataList.isEmpty()) return list
        pData.questDataList.values.forEach {
            if (it.state == QuestState.DOING) {
                if (!(checkMode && !it.questID.matchQuestMode(player))) {
                    list.add(it)
                }
            }
        }
        return list
    }

    fun quitQuest(player: Player, questUUID: UUID) {
        val uuid = player.uniqueId
        val questData = getQuestData(player, questUUID)?: return run {
            player.sendLang("QUEST-NULL_QUEST_DATA", questUUID) }
        val pData = DataStorage.getPlayerData(uuid)
        val questList = pData.questDataList
        val questID = questData.questID
        val questModule = getQuestModule(questID)?: return
        if (questModule.mode.type == ModeType.COLLABORATION) {
            val tData = player.teamData()?: run { questList.remove(questUUID); return }
            tData.members.forEach {
                if (uuid == it) return@forEach
                val mData = DataStorage.getPlayerData(it)
                val mQuestList = mData.questDataList
                if (!mQuestList.containsKey(questUUID)) return@forEach
                val mControl = mData.controlData
                val m = Bukkit.getPlayer(it)?: return@forEach
                val mQuestData = getQuestData(uuid, questID)?: return@forEach
                databaseRemove(m, questUUID, mQuestData, mControl)
                mQuestList.remove(questUUID)
            }
        }
        databaseRemove(player, questUUID, questData, pData.controlData)
        questList.remove(questUUID)
    }

    /**
     * 放弃和清空任务数据
     */
    fun quitQuest(player: Player, questID: String) {
        val uuid = player.uniqueId
        val questData = getQuestData(uuid, questID)?: return run {
            player.sendLang("QUEST-NULL_QUEST_DATA", questID) }
        quitQuest(player, questData.questUUID)
    }

    private fun databaseRemove(player: Player,
                               questUUID: UUID,
                               questData: QuestData, controlData: ControlData) {
        databaseRemoveControl(player, questData.questID, controlData)
        databaseRemoveInner(player, questUUID)
        databaseRemoveQuest(player, questData)
    }

    private fun databaseRemoveQuest(player: Player, questData: QuestData) {
        Database.database.removeQuest(player, questData)
    }

    private fun databaseRemoveInner(player: Player, questUUID: UUID) {
        Database.database.removeInnerQuest(player, questUUID)
    }

    private fun databaseRemoveControl(player: Player, questID: String, controlData: ControlData) {
        val pData = DataStorage.getPlayerData(player)
        pData.controlData.controls.values.forEach {
            if (it.controlID.startsWith("$questID-")) {
                Database.database.removeControl(player, it.controlID)
                controlData.highestControls.remove(it.controlID)
            }
        }
        pData.controlData.highestControls.values.forEach {
            if (it.controlID.startsWith("$questID-")) {
                Database.database.removeControl(player, it.controlID)
                controlData.controls.remove(it.controlID)
            }
        }
    }

    /**
     * 删除任务模块和文件
     */
    fun delQuest(questID: String) {
        if (!questMap.containsKey(questID)) return
        questMap.remove(questID)
        val file = FileUtil.getFileList(FileUtil.getFile("space/quest"))
        val list = file.iterator()
        while (list.hasNext()) {
            val f = list.next()
            val yaml = Configuration.loadFromFile(f)
            if (yaml.contains("quest.questID")) {
                if (yaml.getString("quest.questID") == questID) {
                    val e = newFile(f.path.replace("\\setting.yml", ""), create = false, folder = true)
                    e.deepDelete()
                    return
                }
            }
        }
    }

    /**
     * 删除内部任务文件
     */
    fun delInner(questID: String, innerID: String) {
        val questModule = getQuestModule(questID) ?: return
        val i = questModule.innerQuestList.iterator()
        while (i.hasNext()) {
            val inner = i.next()
            if (inner.id == innerID) {
                i.remove(); break
            }
        }
        val questFolder = FileUtil.getFile("space/quest")
        val lists = questFolder.listFiles() ?: return
        for (file in lists) {
            if (!file.isDirectory) continue
            val settingFile = File(file.path + File.separator + "setting.yml")
            if (!settingFile.exists()) return
            val setting = Configuration.loadFromFile(settingFile)
            if (setting.getString("quest.questID") == questID) {
                if (innerID.isNotEmpty()) {
                    val innerFolder = FileUtil.getFile("space/quest/" + file.name)
                    val innerList = FileUtil.getFileList(innerFolder).iterator()
                    while (innerList.hasNext()) {
                        val inner = innerList.next()
                        val innerYaml = Configuration.loadFromFile(inner)
                        if (innerYaml.contains("inner.id") && innerYaml.getString("inner.id") == innerID) {
                            inner.deepDelete()
                        }
                    }
                }
            }
        }
    }

    /**
     * 保存配置
     */
    fun saveFile(questID: String, innerID: String = "", create: Boolean = false, innerCreate: Boolean = false) {
        if (create) {
            val file = newFile(File(QuestEngine.plugin.dataFolder, "/space/quest/$questID"), folder = true)
            val questModule = QuestModule()
            questModule.questID = questID
            val setting = newFile(file.path+"/setting.yml")
            val yaml = Configuration.loadFromFile(setting)
            yaml.setObject("quest", questModule)
            yaml.saveToFile(setting)
            register(questID, questModule)
        }else if (innerCreate){
            val fileData = questFile(questID)?: return
            val questModule = getQuestModule(questID)?: return
            val inner = QuestInnerModule()
            inner.id=innerID
            questModule.innerQuestList.add(inner)
            val file = fileData.file
            val innerFile = newFile(file.path+"/$innerID.yml")
            val innerYaml = Configuration.loadFromFile(innerFile)
            innerYaml.setObject("inner", inner)
            innerYaml.saveToFile(innerFile)
        }else {
            val fileData = questFile(questID)?: return
            val questModule = getQuestModule(questID)?: return
            val file = fileData.file
            val setting = fileData.configuration
            setting.setObject("quest", questModule)
            val settingFile = File(file.path + File.separator + "setting.yml")
            setting.saveToFile(settingFile)
            if (innerID.isNotEmpty()) {
                val innerFolder = FileUtil.getFile("space/quest/" + file.name)
                val innerList = FileUtil.getFileList(innerFolder)
                for (inner in innerList) {
                    val innerYaml = Configuration.loadFromFile(inner)
                    if (innerYaml.contains("inner.id") && innerYaml.getString("inner.id") == innerID) {
                        questModule.innerQuestList.forEach {
                            if (it.id == innerID) {
                                innerYaml.setObject("inner", it)
                                innerYaml.saveToFile(inner)
                                return
                            }
                        }
                    }
                }
            }
        }
    }

    fun questFile(questID: String): FileData? {
        val questFolder = FileUtil.getFile("space/quest")
        val lists = questFolder.listFiles()?: return null
        for (file in lists) {
            if (!file.isDirectory) continue
            val settingFile = File(file.path + File.separator + "setting.yml")
            if (!settingFile.exists()) return null
            val setting = Configuration.loadFromFile(settingFile)
            if (setting.getString("quest.questID") == questID) {
                return FileData(file, setting)
            }
        }
        return null
    }

    fun getTargetModule(questID: String, innerID: String, id: String): QuestTarget? {
        val inner = getInnerQuestModule(questID, innerID)?: return null
        inner.target.forEach { if (it.id == id) return it }
        return null
    }

    fun getDoingTargets(player: Player, name: String): List<TargetData> {
        val list = mutableListOf<TargetData>()
        getDoingQuest(player).forEach {
            if (it.questInnerData.state == QuestState.DOING) {
                it.questInnerData.targetsData.values.forEach { t ->
                    if (t.name == name) list.add(t)
                }
            }
        }
        return list
    }

}

class FileData(val file: File, val configuration: Configuration)