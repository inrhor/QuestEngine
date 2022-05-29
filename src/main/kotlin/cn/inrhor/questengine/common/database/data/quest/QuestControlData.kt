package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.api.quest.control.ControlPriority
import cn.inrhor.questengine.script.kether.runEval
import org.bukkit.entity.Player

/**
 * 控制模块数据
 */
class QuestControlData(
    val player: Player,
    val controlID: String,
    val controlPriority: ControlPriority,
    var controlList: String,
    var line: Int) {

    constructor(player: Player, controlID: String, controlPriority: ControlPriority, controlList: String):
            this(player, controlID, controlPriority, controlList, 0)

    fun runScript() {
        runEval(player, controlList)
    }

}