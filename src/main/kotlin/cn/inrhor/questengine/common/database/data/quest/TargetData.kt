package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.QuestTarget
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.utlis.public.MsgUtil
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

/**
 * 任务目标存储
 */
class TargetData(val name: String, var timeUnit: String,
                 var schedule: Int, val questTarget: QuestTarget, var timeDate: Date, var endTimeDate: Date?) {

    fun runTime(player: Player, questUUID: UUID) {
        if (timeUnit == "" || endTimeDate == null) return

        object : BukkitRunnable() {
            override fun run() {
                if (!player.isOnline) {
                    cancel(); return
                }
                val now = Date()
                val between = endTimeDate!!.time - now.time
                MsgUtil.send("between $between")
                if (between <= 0) {
                    QuestManager.endQuest(player, questUUID, QuestState.FAILURE, true)
                    MsgUtil.send("end Quest")
                    cancel(); return
                }
            }
        }.runTaskTimerAsynchronously(QuestEngine.plugin, 0, 20L)
    }

}