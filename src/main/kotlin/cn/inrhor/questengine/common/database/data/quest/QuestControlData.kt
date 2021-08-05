package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.database.data.ControlData
import cn.inrhor.questengine.common.quest.ControlPriority
import cn.inrhor.questengine.script.kether.KetherHandler
import cn.inrhor.questengine.script.kether.expand.control.ControlTaskType
import cn.inrhor.questengine.script.kether.expand.control.ControlType
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class QuestControlData(
    val player: Player,
    val controlData: ControlData,
    val controlID: String,
    val controlPriority: ControlPriority,
    val script: MutableList<String>,
    var line: Int,
    var waitTime: Int) {

    constructor(player: Player, controlData: ControlData, controlID: String, controlPriority: ControlPriority, script: MutableList<String>):
            this(player, controlData, controlID, controlPriority, script, 0, 0)

    var time = 0

    fun runScript() {
        if (script.isEmpty() || line >= script.size) {
            if (controlPriority == ControlPriority.HIGHEST) {
                controlData.highestQueue(controlID)
            }else {
                controlData.removeNormal(controlID)
            }
            return
        }
        val content = script[line]
        if (ControlTaskType.returnType(content) == ControlType.ASY) {
            asyRunScript(content)
        }else synRunScript(content)
        line++
    }

    private fun synRunScript(content: String) {
        object : BukkitRunnable() {
            override fun run() {
                if (!player.isOnline) {
                    cancel(); return
                }
                eval(content)
            }
        }.runTaskLater(QuestEngine.plugin, waitTime.toLong())
    }

    private fun asyRunScript(content: String) {
        object : BukkitRunnable() {
            override fun run() {
                if (!player.isOnline) {
                    cancel()
                    return
                }
                eval(content)
            }
        }.runTaskLaterAsynchronously(QuestEngine.plugin, waitTime.toLong())
    }

    private fun eval(content: String) {
        KetherHandler.eval(player, content)
        runScript()
        waitTime = 0
    }

}