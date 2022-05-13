package cn.inrhor.questengine.api.quest.module.inner

import cn.inrhor.questengine.api.ui.UiFrame
import cn.inrhor.questengine.common.database.data.quest.QuestInnerData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.script.kether.runEval
import cn.inrhor.questengine.utlis.variableReader
import org.bukkit.entity.Player
import java.text.SimpleDateFormat
import java.util.*


class QuestTarget(val id: String, val name: String, var time: TimeFrame, val reward: String,
                  var period: Int, var async: Boolean, var condition: List<String>,
                  val node: String, val ui: UiFrame
) {
    constructor():
            this("targetId", "targetName", TimeFrame(), "", 0, false, listOf(), "", UiFrame())

    fun nodeMeta(meta: String): List<String>? {
        node.variableReader().forEach {
            val sp = it.split(" ")
            if (sp[0].uppercase() == meta.uppercase()) {
                val l = sp.toMutableList()
                l.removeAt(0)
                return l.toList()
            }
        }
        return null
    }

    fun timeAccept(player: Player, innerModule: QuestInnerModule, innerData: QuestInnerData) {
        val sp = time.duration.split(">")
        val a = sp[0].split(",")
        val b = sp[1].split(",")
        val dateFormat = SimpleDateFormat("HH:mm:ss")
        when (time.type) {
            TimeFrame.Type.DAY -> {
                val start = dateFormat.parse(a[0])
                val end = dateFormat.parse(b[0])
                val now = Date()
                if (time.noTimeout(now, start, end)) {
                    if (innerData.targetsData.containsKey(id)) {
                        val inD = innerData.targetsData[id]!!
                        if (inD.state == QuestState.FAILURE) {
                            if (QuestManager.hasStateInnerQuest(player, QuestState.DOING)) {
                                inD.state = QuestState.IDLE
                            }else {

                            }
                        }
                    }
                }else {
                    if (innerData.targetsData.containsKey(id)) {
                        val inD = innerData.targetsData[id]!!
                        if (inD.state != QuestState.FAILURE) {
                            inD.state = QuestState.FAILURE
                            runEval(player, innerModule.reward.fail)
                        }
                    }
                }
            }
            TimeFrame.Type.WEEKLY -> {

            }
            else -> return
        }
    }
}
