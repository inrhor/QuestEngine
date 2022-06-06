package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.quest.module.inner.QuestModule
import cn.inrhor.questengine.api.quest.module.inner.QuestTarget
import cn.inrhor.questengine.api.quest.module.group.GroupModule
import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.ControlQueue
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.database.data.PlayerData
import cn.inrhor.questengine.common.database.data.quest.*
import cn.inrhor.questengine.common.database.data.teamData
import cn.inrhor.questengine.common.quest.ModeType
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.manager.QuestManager.getGroupData
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
    var questMap = mutableMapOf<String, GroupModule>()

    /**
     * 自动接受的任务模块内容
     */
    var autoQuestMap = mutableMapOf<String, GroupModule>()

    /**
     * 注册任务模块内容
     */
    fun register(questID: String, questModule: GroupModule, sort: String = "") {
        questMap[questID] = questModule
        questModule.questList.forEach {
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
     * @return 任务组模块
     */
    fun String.getGroupModule(): GroupModule {
        return questMap[this]?: error("group no register")
    }

    /**
     * @return 任务模块
     */
    fun String.getQuestModule(groupID: String): QuestModule? {
        questMap[groupID]?.questList?.forEach {
            if (it.id == this) return it
        }
        return null
    }

    /**
     * 精准检索玩家任务组数据
     */
    fun UUID.getGroupData(pUid: UUID): GroupData {
        pUid.getPlayerData().dataContainer.group.forEach {
            if (it.uuid == this) return it
        }
        error("null group data")
    }

    /**
     * 根据 QuestID 模糊检索玩家任务组数据
     */
    fun String.getGroupData(pUid: UUID): GroupData {
        pUid.getPlayerData().dataContainer.group.forEach {
            if (it.id == this) return it
        }
        error("null group data")
    }

    /**
     * @return Group UUID 返回 ID
     */
    fun UUID.getGroupID(player: Player): String {
        return this.getGroupData(player.uniqueId).id
    }

    /**
     * @return 玩家是否满足任务组模式
     * @param share 是否判断共享数据
     */
    fun GroupModule.matchMode(player: Player, share: Boolean = true): Boolean {
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
        return this.getGroupModule().matchMode(player, share)
    }

    /**
     * @return 玩家是否满足任务组模式
     * @param share 是否判断共享数据
     */
    fun UUID.matchMode(player: Player, share: Boolean = true): Boolean {
        return this.getGroupID(player).getGroupModule().matchMode(player, share)
    }

    /**
     * @return 任务组队伍模式
     */
    fun String.getQuestMode(): ModeType {
        return this.getGroupModule().mode.type
    }

    /**
     * @return 是否存在任务组数据
     */
    fun UUID.existGroupData(player: Player): Boolean {
        player.getPlayerData().dataContainer.group.forEach {
            if (it.uuid == this) return true
        }
        return false
    }

}