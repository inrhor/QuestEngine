package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.api.quest.control.ControlPriority

/**
 * 控制模块数据
 */
class ControlData(
    val id: String = "?",
    val priority: ControlPriority = ControlPriority.NORMAL,
    var script: String = "",
    var line: Int = 0)