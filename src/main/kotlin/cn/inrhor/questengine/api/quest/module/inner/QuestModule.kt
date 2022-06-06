package cn.inrhor.questengine.api.quest.module.inner

import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.script.kether.runEval
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import java.util.*

class QuestModule(var id: String, var name: String,
                  var description: List<String>,
                  var finish: String,
                  var fail: String,
                  var time: TimeFrame,
                  var control: MutableList<QuestControl>,
                  var target: MutableList<QuestTarget>) {
    constructor(): this("innerIDNull", "null inner name", listOf(), "", "",
        TimeFrame(), mutableListOf(), mutableListOf())

    fun existTargetID(id: String): Boolean {
        target.forEach { if (it.id == id) return true }
        return false
    }

    fun delTarget(id: String) {
        val i = target.iterator()
        while (i.hasNext()) {
            val n = i.next()
            if (n.id== id) {
                i.remove()
                break
            }
        }
    }

    fun existControlID(id: String): Boolean {
        control.forEach { if (it.id == id) return true }

        return false
    }

    private fun acceptInner(player: Player, innerData: QuestData) {
        if (innerData.state == QuestState.FAILURE) {
            QuestManager.acceptInnerQuest(player, innerData.questID, id, false)
        }
    }

    private fun timeout(player: Player, innerData: QuestData) {
        if (innerData.state != QuestState.FAILURE) {
            innerData.state = QuestState.FAILURE
            runEval(player, fail)
        }
    }

    /**
     * 定时任务
     */
    fun timeAccept(player: Player, innerModule: QuestModule, innerData: QuestData) {
        if (time.type != TimeFrame.Type.ALWAYS) {
            innerData.updateTime(innerModule.time)
            submit(period = 20L, async = true) {
                if (innerData.end == null || !player.isOnline || time.type == TimeFrame.Type.ALWAYS) {
                    cancel();return@submit
                }else {
                    val now = Date()
                    if (!time.noTimeout(now, innerData.timeDate, innerData.end!!)) {
                        innerData.updateTime(innerModule.time)
                        if (time.noTimeout(now, innerData.timeDate, innerData.end!!)) {
                            acceptInner(player, innerData)
                            cancel()
                            return@submit
                        }else {
                            timeout(player, innerData)
                        }
                    }
                }
            }
        }
    }

}