package cn.inrhor.questengine.api.quest

import cn.inrhor.questengine.common.record.QuestRecord

data class GroupFrame(
    @Transient override var id: String = "?",
    var name: String = "", var note: List<String> = listOf(),
    var quest: List<String> = listOf(), var data: List<String> = listOf(),
    @Transient val quests: MutableList<QuestFrame> = mutableListOf()
): QuestRecord.ActionFunc
