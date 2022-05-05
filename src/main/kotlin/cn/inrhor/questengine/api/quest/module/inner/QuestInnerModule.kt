package cn.inrhor.questengine.api.quest.module.inner

import cn.inrhor.questengine.api.quest.control.QuestControlOpen

class QuestInnerModule(val id: String, var name: String,
                       var nextInnerQuestID: String,
                       var description: List<String>,
                       var reward: QuestReward,
                       var questControl: List<QuestControlOpen>,
                       var questTargetList: List<QuestTarget>) {
    constructor(): this("innerIDNull", "null inner name", "",listOf(), QuestReward(), listOf(), listOf())
}