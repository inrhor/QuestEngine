package cn.inrhor.questengine.common.hook.invero

import cc.trixey.invero.common.Invero
import cc.trixey.invero.common.Object
import cc.trixey.invero.common.sourceObject
import cc.trixey.invero.core.Context
import cn.inrhor.questengine.api.quest.GroupFrame
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestFrame
import cn.inrhor.questengine.server.ReadManager.inveroLoad

object InvGenerator {

    fun questGenerate(context: Context, uiType: UiType): List<Object> {
        val group = context.variables["quest_group"] as GroupFrame
        val player = context.player

        return uiType.list(player, group).map {
            val q = it.id.getQuestFrame()
            val data = q?.data?: listOf()
            val note = q?.note?: listOf()
            sourceObject {
                // https://invero.trixey.cc/docs/advance/basic/context
                // context set quest_data to element self_quest
                put("self_quest", it) // 目的传递给下一个菜单
                put("id", it.id)
                put("self_data", data)
                put("self_note", note)
            }
        }
    }

    fun groupGenerate(context: Context, uiType: UiType): List<Object> {
        val player = context.player

        return uiType.list(player).map {
            sourceObject {
                // context set quest_group to element self_group
                put("self_group", it)
                put("id", it.id)
                put("self_data", it.data)
                put("self_note", it.note)
            }
        }
    }

    fun targetGenerate(context: Context, uiType: UiType): List<Object> {
        val quest = context.variables["quest_data"] as QuestData
        val player = context.player

        return uiType.list(player, quest).map {
            val t = it.getTargetFrame()
            val data = t?.data?: listOf()
            val note = t?.description?: listOf()
            sourceObject {
                put("id", it.id)
                put("self_data", data)
                put("self_note", note)
            }
        }
    }

    fun load() {
        if (inveroLoad) {
            val api = Invero.API.getRegistry()
            val a = "QuestEngine"
            api.registerElementGenerator(a, UiType.QUEST_DOING.uiName(), QuestDoingGenerator())
            api.registerElementGenerator(a, UiType.QUEST_COMPLETE.uiName(), QuestCompleteGenerator())
            api.registerElementGenerator(a, UiType.GROUP_DOING.uiName(), GroupDoingGenerator())
            api.registerElementGenerator(a, UiType.GROUP_COMPLETE.uiName(), GroupCompleteGenerator())
            api.registerElementGenerator(a, UiType.TARGET_DOING.uiName(), TargetDoingGenerator())
            api.registerElementGenerator(a, UiType.TARGET_COMPLETE.uiName(), TargetCompleteGenerator())
        }
    }

}