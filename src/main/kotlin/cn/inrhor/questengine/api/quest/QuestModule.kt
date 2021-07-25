package cn.inrhor.questengine.api.quest

import cn.inrhor.questengine.common.quest.ModeType

class QuestModule(val questID: String,
                  var name: String,
                  var startInnerQuestID: String,
                  var modeType: ModeType,
                  var modeAmount: Int,
                  var modeShareData: Boolean,
                  var acceptCheck: Int, var acceptCondition: MutableList<String>,
                  var failCheck: Int, var failCondition: MutableList<String>,
                  var innerQuestList: MutableList<QuestInnerModule>) {

    fun getStartInnerQuest(): QuestInnerModule? {
        innerQuestList.forEach {
            if (it.innerQuestID == startInnerQuestID) return it
        }
        return null
    }

}