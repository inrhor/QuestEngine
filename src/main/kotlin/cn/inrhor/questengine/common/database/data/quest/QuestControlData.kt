package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.script.kether.KetherHandler
import cn.inrhor.questengine.common.script.kether.expand.control.ControlTaskType
import cn.inrhor.questengine.common.script.kether.expand.control.ControlType
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class QuestControlData(
    val player: Player,
    val controlID: String,
    val script: MutableList<String>,
    var line: Int,
    var waitTime: Int) {

    fun runScript() {
        if (script.isEmpty()) return
        val content = script[line]
        if (ControlTaskType.returnType(content) == ControlType.ASY) {
            asyRunScript(content)
        }else synRunScript(content)
        line++
    }

    private fun synRunScript(content: String) {
        object : BukkitRunnable() {
            override fun run() {
                KetherHandler.eval(player, content)
            }
        }.runTaskLater(QuestEngine.plugin, waitTime.toLong())
    }

    private fun asyRunScript(content: String) {
        object : BukkitRunnable() {
            override fun run() {
                KetherHandler.eval(player, content)
            }
        }.runTaskLaterAsynchronously(QuestEngine.plugin, waitTime.toLong())
    }

}