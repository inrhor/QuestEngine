package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.script.kether.KetherHandler
import cn.inrhor.questengine.script.kether.expand.control.ControlTaskType
import cn.inrhor.questengine.script.kether.expand.control.ControlType
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class QuestControlData(
    val player: Player,
    val questOpenData: QuestOpenData,
    val script: MutableList<String>,
    var line: Int,
    var waitTime: Int) {

    var time = 0

    fun runScript() {
        if (script.isEmpty() || line >= script.size) return
        val content = script[line]
        if (ControlTaskType.returnType(content) == ControlType.ASY) {
            asyRunScript(content)
        }else synRunScript(content)
        line++
    }

    private fun synRunScript(content: String) {
        object : BukkitRunnable() {
            override fun run() {
                if (!player.isOnline || questOpenData.state != QuestState.DOING) {
                    cancel()
                    return
                }
                eval(content)
            }
        }.runTaskLater(QuestEngine.plugin, waitTime.toLong())
    }

    private fun asyRunScript(content: String) {
        object : BukkitRunnable() {
            override fun run() {
                if (!player.isOnline || questOpenData.state != QuestState.DOING) {
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