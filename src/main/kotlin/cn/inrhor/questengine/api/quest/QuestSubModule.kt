package cn.inrhor.questengine.api.quest

import cn.inrhor.questengine.common.quest.QuestControl
import cn.inrhor.questengine.common.quest.QuestReward

class QuestSubModule(val mainQuestID: String,
                     var questControl: QuestControl,
                     var questReward: QuestReward,
                     var questTargetList: MutableList<TargetExtend<*>>) {

}