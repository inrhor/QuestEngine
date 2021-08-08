package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.common.database.data.ControlData
import cn.inrhor.questengine.common.quest.ControlPriority
import cn.inrhor.questengine.script.kether.eval
import cn.inrhor.questengine.script.kether.expand.control.ControlTaskType
import cn.inrhor.questengine.script.kether.expand.control.ControlType
import org.bukkit.entity.Player
import taboolib.common.platform.submit

class QuestControlData(
    val player: Player,
    val controlData: ControlData,
    val controlID: String,
    val controlPriority: ControlPriority,
    var controlList: MutableList<String>,
    var line: Int,
    var waitTime: Int) {

    constructor(player: Player, controlData: ControlData, controlID: String, controlPriority: ControlPriority, controlList: MutableList<String>):
            this(player, controlData, controlID, controlPriority, controlList, 0, 0)

    var time = 0

    fun runScript() {
        if (controlList.isEmpty() || line >= controlList.size) {
            if (controlPriority == ControlPriority.HIGHEST) {
                controlData.highestQueue(controlID)
            }else {
                controlData.removeNormal(controlID)
            }
            return
        }
        val content = controlList[line]
        if (ControlTaskType.returnType(content) == ControlType.ASY) {
            asyRunScript(content)
        }else synRunScript(content)
        line++
    }

    private fun synRunScript(content: String) {
        submit(delay = waitTime.toLong()) {
            if (!player.isOnline) {
                cancel(); return@submit
            }
            evalShell(content)
        }
    }

    private fun asyRunScript(content: String) {
        submit(async = true, delay = waitTime.toLong()) {
            if (!player.isOnline) {
                cancel(); return@submit
            }
            evalShell(content)
        }
    }

    private fun evalShell(content: String) {
        eval(player, content)
        runScript()
        waitTime = 0
    }

}