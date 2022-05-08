package cn.inrhor.questengine.api.quest.module.inner

class QuestInnerModule(val id: String, var name: String,
                       var nextInnerQuestID: String,
                       var description: List<String>,
                       var reward: QuestReward,
                       var control: List<QuestControl>,
                       var target: List<QuestTarget>) {
    constructor(): this("innerIDNull", "null inner name", "",listOf(), QuestReward(), listOf(), listOf())
}