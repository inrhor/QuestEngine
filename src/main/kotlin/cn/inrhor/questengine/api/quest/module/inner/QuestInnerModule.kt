package cn.inrhor.questengine.api.quest.module.inner

class QuestInnerModule(var id: String, var name: String,
                       var nextInnerQuestID: String,
                       var description: List<String>,
                       var reward: QuestReward,
                       var control: MutableList<QuestControl>,
                       var target: MutableList<QuestTarget>) {
    constructor(): this("innerIDNull", "null inner name", "",listOf(), QuestReward(), mutableListOf(), mutableListOf())
}