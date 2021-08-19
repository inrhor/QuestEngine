/*
package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.script.kether.evalBoolean
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit

*/
/**
 * @param name 任务目标名称
 * @param conditions 任务目标要求
 *//*

class TargetTaskData(
    val player: Player, val innerData: QuestInnerData,
    val name: String, val conditions: MutableList<String>,
    private val period: Long, private val async: Boolean) {

    init {
        submit(async = this.async, period = this.period) {
            if (innerData.state != QuestState.DOING || !player.isOnline) {
                cancel()
                return@submit
            }
            if (evalBoolean()) {

            }
        }
    }

}*/
