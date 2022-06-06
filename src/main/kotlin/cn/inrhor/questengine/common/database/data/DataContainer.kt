package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.common.database.data.quest.GroupData

data class DataContainer(
    var group: MutableList<GroupData> = mutableListOf(), var tags: TagsData = TagsData()
)
