package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.quest.manager.QuestManager.finishTarget
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager.matchMode
import cn.inrhor.questengine.script.kether.runEval
import cn.inrhor.questengine.script.kether.runEvalSet
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit

/**
 * 任务目标存储
 */
data class TargetData(
    val id: String ="?",
    val questID: String = "?",
    var schedule: Int = 0,
    var state: StateType = StateType.DOING) {

    constructor(questID: String, target: TargetFrame): this(target.id, questID)

    /**
     * @return 目标模块
     */
    fun getTargetFrame(): TargetFrame {
        questID.getQuestFrame().target.forEach {
            if (it.id == id) return it
        }
        error("null target frame: $id($questID)")
    }

    fun load(player: Player) {
        if (state == StateType.DOING) {
            val target = getTargetFrame()
            if (target.name.uppercase().startsWith("TASK ")) {
                target.task(player)
            }
        }
    }

    private fun TargetFrame.task(player: Player) {
        val quest = questID.getQuestFrame()
        submit(async = async, period = period.toLong()) {
            if (!player.isOnline || state != StateType.DOING) {
                cancel(); return@submit
            }
            if (quest.matchMode(player) && runEvalSet(quest.mode.type.objective(player), condition)) {
                player.finishTarget(this@TargetData, quest.mode.type)
            }
        }
    }

}