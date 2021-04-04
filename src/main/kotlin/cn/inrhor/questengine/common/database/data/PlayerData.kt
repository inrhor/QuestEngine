package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.common.dialog.cube.ClickBox
import java.util.*

class PlayerData(
    val uuid: UUID,
    var clickBoxList: MutableList<ClickBox>,
    var mainQuestList: MutableList<String>,
    var controlList: MutableList<String>) {

    constructor(uuid: UUID):
            this(uuid, mutableListOf(), mutableListOf(), mutableListOf())



}