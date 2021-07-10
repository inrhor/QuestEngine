package cn.inrhor.questengine.api.quest

class QuestModule(val questID: String,
                  var name: String,
                  var startMainQuestID: String,
                  var modeType: String,
                  var modeAmount: Int,
                  var acceptCheck: Int, var acceptCondition: MutableList<String>,
                  var failCheck: Int, var failCondition: MutableList<String>,
                  var mainQuestList: MutableList<QuestMainModule>) {

    fun getStartMainQuest(): QuestMainModule? {
        mainQuestList.forEach {
            if (it.mainQuestID == startMainQuestID) return it
        }
        return null
    }

}