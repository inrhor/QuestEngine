package cn.inrhor.questengine.api.quest

import cn.inrhor.questengine.common.quest.ModeType

class QuestModule(val questID: String,
                  var name: String,
                  var startMainQuestID: String,
                  var modeType: ModeType,
                  var modeAmount: Int,
                  var modeShareData: Boolean,
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