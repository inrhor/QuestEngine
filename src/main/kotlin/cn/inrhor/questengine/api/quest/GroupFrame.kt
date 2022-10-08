package cn.inrhor.questengine.api.quest

data class GroupFrame(var name: String = "", var note: List<String> = listOf(),
    var quest: List<String> = listOf(), var data: List<String> = listOf(),
                      @Transient val quests: MutableList<QuestFrame> = mutableListOf()
)
