package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.common.database.data.quest.QuestData

data class DataContainer(
    var quest: MutableMap<String, QuestData> = mutableMapOf(), var tags: TagsData = TagsData()
)
