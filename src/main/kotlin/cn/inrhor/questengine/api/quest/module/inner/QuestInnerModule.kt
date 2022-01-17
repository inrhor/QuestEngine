package cn.inrhor.questengine.api.quest.module.inner

import cn.inrhor.questengine.api.quest.control.QuestControlOpen
import taboolib.library.configuration.PreserveNotNull

class QuestInnerModule(
    @PreserveNotNull val id: String = "", @PreserveNotNull val name: String = "null name",
    @PreserveNotNull val nextInnerQuestID: String = "",
    @PreserveNotNull val description: List<String> = listOf(),
    @PreserveNotNull var questControl: MutableList<QuestControlOpen> = mutableListOf(),
    @PreserveNotNull var reward: QuestReward = QuestReward(),
    @PreserveNotNull var questTargetList: MutableMap<String, QuestTarget> = mutableMapOf()
)