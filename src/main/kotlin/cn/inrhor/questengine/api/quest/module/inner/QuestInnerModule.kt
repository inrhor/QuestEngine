package cn.inrhor.questengine.api.quest.module.inner

import cn.inrhor.questengine.api.quest.control.QuestControlOpen
import taboolib.library.configuration.PreserveNotNull

class QuestInnerModule(val id: String, val name: String,
                       val nextInnerQuestID: String,
                       val description: List<String>,
                       var reward: QuestReward,
                       @Transient var questControl: MutableList<QuestControlOpen> = mutableListOf(),
                       @Transient var questTargetList: MutableMap<String, QuestTarget> = mutableMapOf()) {
    constructor(): this("innerIDNull", "null inner name", "",listOf(), QuestReward())
}