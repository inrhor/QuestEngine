package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.api.quest.ControlFrame
import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.database.data.teamData
import cn.inrhor.questengine.common.quest.enum.ModeType
import cn.inrhor.questengine.common.quest.ui.QuestBookBuildManager
import org.bukkit.entity.Player

object QuestManager {

    /**
     * 注册的任务模块内容
     */
    private var questMap = mutableMapOf<String, QuestFrame>()

    /**
     * 自动接受的任务模块内容
     */
    private var autoQuestMap = mutableMapOf<String, QuestFrame>()

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
        QuestBookBuildManager.addSortQuest(group.sort, this)
    }

    /**
     * @return 任务模块
     */
    fun String.getQuestFrame(): QuestFrame {
        return questMap[this]?: error("null quest frame: $this")
    }

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
        return getQuestFrame().matchMode(player, share)
    }

    /**
     * @return 任务组队伍模式
     */
    fun String.getQuestMode(): ModeType {
        return getQuestFrame().mode.type
    }

    /**
     * @return 控制模块
     */
    fun String.getControlFrame(questID: String): ControlFrame {
        questID.getQuestFrame().control.forEach { if (it.id == this) return it }
        error("null control frame: $this($questID)")
    }

}