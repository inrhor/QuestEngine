package cn.inrhor.questengine.common.database.data

import java.util.*

class PlayerData(
    val uuid: UUID,
    /*var clickBoxList: MutableList<ClickBox>,*/
    var mainQuestList: MutableList<String>,
    var controlList: MutableList<String>/*,
    var clickBoxData: PlayerClickBoxData*/
) {

    constructor(uuid: UUID):
            this(uuid, /*mutableListOf(),*/ mutableListOf(), mutableListOf(),
                /*PlayerClickBoxData(uuid, mutableListOf())*/)

}