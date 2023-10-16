package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.api.event.QuestEvent
import cn.inrhor.questengine.api.event.TargetEvent
import cn.inrhor.questengine.api.manager.DataManager.questData
import cn.inrhor.questengine.api.manager.DataManager.targetData
import cn.inrhor.questengine.api.quest.ControlFrame
import cn.inrhor.questengine.api.quest.GroupFrame
import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.api.manager.DataManager.teamData
import cn.inrhor.questengine.common.quest.enum.ModeType
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.script.kether.runEval
import org.bukkit.entity.Player
import taboolib.common.io.deepDelete
import taboolib.common.io.newFile
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Configuration.Companion.setObject

object QuestManager {

    /**
     * 注册的任务模块内容
     */
    private val questMap = mutableMapOf<String, QuestFrame>()

    /**
     * 任务组模块
     */
    val groupMap = mutableMapOf<String, GroupFrame>()

    fun String.getGroupFrame(): GroupFrame? = groupMap[this]

    /**
     * 自动接受的任务模块内容
     */
    var autoQuestMap = mutableMapOf<String, QuestFrame>()

    fun getQuestMap() = questMap

    fun clearQuestMap() = questMap.clear()

    /**
     * 注册任务模块内容
     */
    fun QuestFrame.register() {
        questMap[id] = this
        target.forEach {
            it.loadNode()
        }
        if (accept.auto) {
            autoQuestMap[id] = this
        }
        time.regUpdateTime()
    }

    /**
     * 保存任务配置
     */
    fun QuestFrame.saveFile(create: Boolean = false) {
        val file = newFile(path, create)
        val yaml = Configuration.loadFromFile(file)
        yaml.setObject("quest", this)
        yaml.saveToFile(file)
    }

    /**
     * 保存任务配置
     */
    fun String.saveQuestFile() {
        getQuestFrame()?.saveFile()
    }

    /**
     * @return 任务模块
     */
    fun String.getQuestFrame(): QuestFrame? {
        return questMap[this]
    }

    /**
     * 删除任务配置
     */
    fun QuestFrame.delFile() {
        newFile(path).deepDelete()
        questMap.remove(id)
    }

    /**
     * 删除任务配置
     */
    fun String.delQuestFile() {
        getQuestFrame()?.delFile()
    }

    /**
     * @return 是否存在任务模块
     */
    fun String.existQuestFrame() = questMap.containsKey(this)

    /**
     * @return 玩家是否满足任务组模式
     * @param share 是否判断共享数据
     */
    fun QuestFrame.matchMode(player: Player, share: Boolean = true): Boolean {
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
     * @return 玩家是否满足任务组模式
     * @param share 是否判断共享数据
     */
    fun String.matchMode(player: Player, share: Boolean = true): Boolean {
        return getQuestFrame()?.matchMode(player, share)?: false
    }

    /**
     * @return 任务组队伍模式
     */
    fun String.getQuestMode(): ModeType {
        return getQuestFrame()?.mode?.type?: ModeType.PERSONAL
    }

    /**
     * @return 控制模块
     */
    fun String.getControlFrame(questID: String): ControlFrame? {
        questID.getQuestFrame()?.control?.forEach { if (it.id == this) return it }
        return null
    }

    /**
     * 接受任务
     */
    fun Player.acceptQuest(quest: QuestFrame) {
        val id = quest.id
        delQuest(id)
        if (runEval(this, quest.accept.condition)) {
            getPlayerData().dataContainer.installQuest(quest)
            QuestEvent.Accept(this, quest).call()
            val data = questData(id)?: return
            data.generateTime()
            data.updateTime(this)
            data.target.forEach {
                it.load(this)
            }
        }
    }

    /**
     * 接受任务
     */
    fun Player.acceptQuest(questID: String) {
        val q = questID.getQuestFrame()?: return
        acceptQuest(q)
    }

    /**
     * 放弃任务
     */
    fun Player.quitQuest(questID: String) {
        val q = questID.getQuestFrame()?: return
        getPlayerData().dataContainer.unloadQuest(questID)
        QuestEvent.Quit(this, q).call()
    }

    /**
     * 完成任务
     */
    fun Player.finishQuest(questID: String) {
        val q = questID.getQuestFrame()?: return
        getPlayerData().dataContainer.toggleQuest(questID, StateType.FINISH).finishTime(questID)
        QuestEvent.Finish(this, q).call()
    }

    /**
     * 删除任务
     */
    fun Player.delQuest(questID: String) {
        val quest = questData(questID)?: return
        quest.unload()
        getPlayerData().dataContainer.unloadQuest(questID)
    }

    /**
     * 重置任务
     */
    fun Player.resetQuest(questID: String) {
        val quest = questID.getQuestFrame()?: return
        getPlayerData().dataContainer.installQuest(quest)
        QuestEvent.Reset(this, quest).call()
        questData(quest.id)?.updateTime(this)
    }

    /**
     * 任务失败
     */
    fun Player.failQuest(questID: String) {
        val q = questID.getQuestFrame()?: return
        getPlayerData().dataContainer.toggleQuest(questID, StateType.FAILURE)
        QuestEvent.Fail(this, q).call()
    }

    /**
     * 追踪任务
     */
    fun Player.trackQuest(questID: String) {
        val q = questData(questID)?: return
        val quest = q.id.getQuestFrame()?: return
        QuestEvent.Track(this, quest).call()
    }

    /**
     * 完成目标
     */
    fun Player.finishTarget(targetData: TargetData, modeType: ModeType) {
        TargetEvent.Finish(this, targetData, modeType).call()
    }

    /**
     * 完成目标
     */
    fun Player.finishTarget(questID: String, targetID: String) {
        val targetData = targetData(questID, targetID)?: return
        val quest = questID.getQuestFrame()?: return
        finishTarget(targetData, quest.mode.type)
    }

    /**
     * 追踪目标
     */
    fun Player.trackTarget(questID: String, targetID: String) {
        val targetData = targetData(questID, targetID)?: return
        val quest = questID.getQuestFrame()?: return
        TargetEvent.Track(this, targetData, quest).call()
    }

    /**
     * @return 目标模块
     */
    fun String.getTargetFrame(questID: String): TargetFrame? {
        return questID.getQuestFrame()?.target?.find { it.id == this }
    }

    /**
     * @return 目标模块
     */
    fun QuestFrame.getTargetFrame(targetID: String): TargetFrame? {
        return target.find { it.id == targetID }
    }

}