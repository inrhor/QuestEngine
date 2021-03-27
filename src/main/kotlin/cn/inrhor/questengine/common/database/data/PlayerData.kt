package cn.inrhor.questengine.common.database.data

import java.util.*

class PlayerData(
    val uuid: UUID,
    var mainQuestList: MutableList<String>,
    var controlList: MutableList<String>) {

    constructor(uuid: UUID):
            this(uuid, mutableListOf(), mutableListOf())
}