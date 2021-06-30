package cn.inrhor.questengine.common.database.data

import java.util.*

class PlayerData(
    val uuid: UUID,
    val dialogData: DialogData,
    var mainQuestList: MutableList<String>,
    var controlList: MutableList<String>
) {

    constructor(uuid: UUID):
            this(uuid,
                DialogData(mutableListOf(), mutableListOf(), mutableListOf()),
                mutableListOf(),
                mutableListOf())

}