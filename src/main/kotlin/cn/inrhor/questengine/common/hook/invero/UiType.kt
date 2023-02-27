package cn.inrhor.questengine.common.hook.invero

import cn.inrhor.questengine.api.manager.DataManager.questData
import cn.inrhor.questengine.api.quest.GroupFrame
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player

enum class UiType {

    QUEST_DOING {
        override fun list(player: Player, group: GroupFrame): List<QuestData> {
            return addQuest(player, group, StateType.DOING)
        }
        override fun uiName() = "questDoing"
    },
    QUEST_COMPLETE {
        override fun list(player: Player, group: GroupFrame): List<QuestData> {
            return addQuest(player, group, StateType.FINISH)
        }
        override fun uiName() = "questComplete"
    },
    GROUP_DOING {
        override fun uiName(): String {
            return "groupDoing"
        }

        override fun list(player: Player): List<GroupFrame> {
            return addGroup(player, StateType.DOING)
        }
    },
    GROUP_COMPLETE {
        override fun uiName(): String {
            return "groupComplete"
        }

        override fun list(player: Player): List<GroupFrame> {
            return addGroup(player, StateType.FINISH)
        }
    },
    TARGET_DOING {
        override fun uiName(): String {
            return "targetDoing"
        }

        override fun list(player: Player, questData: QuestData): List<TargetData> {
            return questData.target
        }
    },
    TARGET_COMPLETE {
        override fun uiName(): String {
            return "targetComplete"
        }

        override fun list(player: Player, questData: QuestData): List<TargetData> {
            return questData.target
        }
    };

    protected fun addQuest(player: Player, group: GroupFrame, stateType: StateType): List<QuestData> {
        val list = mutableListOf<QuestData>()
        group.quest.forEach {
            val q = player.questData(it)
            if (q != null && q.state == stateType) list.add(q)
        }
        return list
    }

    protected fun addGroup(player: Player, stateType: StateType): List<GroupFrame> {
        val list = mutableListOf<GroupFrame>()
        QuestManager.groupMap.values.forEach { u ->
            if (u.quest.any { player.questData(it)?.state == stateType }) list.add(u)
        }
        return list
    }

    open fun list(player: Player, group: GroupFrame): List<QuestData> = listOf()

    open fun list(player: Player): List<GroupFrame> = listOf()

    open fun list(player: Player, questData: QuestData): List<TargetData> = listOf()

    abstract fun uiName(): String

}