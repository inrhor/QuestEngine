package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.common.quest.ModeType
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.QuestTarget
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.submit
import java.util.*

/**
 * 任务目标存储
 */
class TargetData(val name: String, var timeUnit: String,
                 var schedule: Int, val questTarget: QuestTarget, var timeDate: Date, var endTimeDate: Date?,
                 var modeType: ModeType) {

    fun runTime(player: Player, questUUID: UUID) {
        if (timeUnit == "" || endTimeDate == null) return
        submit(async = true, period = 20L) {
            if (!player.isOnline) {
                cancel(); return@submit
            }
            val now = Date()
            val between = endTimeDate!!.time - now.time
            if (QuestManager.isStateInnerQuest(player, questUUID, QuestState.FAILURE)) {
                cancel(); return@submit
            }
            if (between <= 0) {
                QuestManager.endQuest(player, modeType, questUUID, QuestState.FAILURE, true)
                cancel(); return@submit
            }
        }
    }

}