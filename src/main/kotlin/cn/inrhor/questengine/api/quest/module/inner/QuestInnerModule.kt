package cn.inrhor.questengine.api.quest.module.inner

import cn.inrhor.questengine.api.quest.control.QuestControlOpen

class QuestInnerModule(
    val id: String, val name: String, val nextInnerQuestID: String, val description: List<String>,
    var questControls: MutableList<QuestControlOpen>,
    var questReward: QuestReward,
    var questTargetList: MutableMap<String, QuestTarget>)