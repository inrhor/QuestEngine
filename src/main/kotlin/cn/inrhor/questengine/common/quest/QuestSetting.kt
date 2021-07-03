package cn.inrhor.questengine.common.quest

class QuestSetting(val questID: String,
                   var name: String,
                   var startMainQuestID: String,
                   var mode: String,
                   var acceptCheck: Int = -1, var acceptCondition: MutableList<String>,
                   var failCheck: Int = -1, var failCondition: MutableList<String>) {

}