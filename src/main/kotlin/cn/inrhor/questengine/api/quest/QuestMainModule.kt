package cn.inrhor.questengine.api.quest

import cn.inrhor.questengine.common.quest.QuestControl
import cn.inrhor.questengine.common.quest.QuestReward
import cn.inrhor.questengine.common.quest.QuestTarget

class QuestMainModule(val mainQuestID: String,
                      val nextMinQuestID: String,
                      var subQuestList: MutableList<QuestSubModule>,
                      var questControl: QuestControl,
                      var questReward: QuestReward,
                      var questTargetList: MutableList<QuestTarget>) {

}