package cn.inrhor.questengine.api.quest

import cn.inrhor.questengine.api.quest.control.QuestControlOpen
import cn.inrhor.questengine.common.quest.QuestReward
import cn.inrhor.questengine.common.quest.QuestTarget

class QuestInnerModule(val innerQuestID: String,
                       val innerQuestName: String,
                       val nextInnerQuestID: String,
                       var questControls: MutableList<QuestControlOpen>,
                       var questReward: QuestReward,
                       var questTargetList: MutableMap<String, QuestTarget>,
                       var description: List<String>) {

}