package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.ModeType
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.api.quest.module.inner.QuestTarget
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.manager.RewardManager
import cn.inrhor.questengine.script.kether.runEval

import org.bukkit.entity.Player
import taboolib.common.platform.function.*
import java.util.*

/**
 * 任务目标存储
 */
class TargetData(
    val questUUID: UUID,
    val innerID: String,
    val name: String, var timeUnit: String,
    var schedule: Int, val questTarget: QuestTarget,
    var timeDate: Date, var endTimeDate: Date?,
    var modeType: ModeType, var state: QuestState = QuestState.DOING) {

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

    /**
     * 启用调度类型的任务目标
     * 备注：要求多人完成的，则判断 成员数 <= 进度
     */
    fun runTask(player: Player, questData: QuestData, innerData: QuestInnerData) {
        var pass = false
        val t = questTarget.period.toLong()
        submit(delay = t, period = t, async = questTarget.async) {
            if (!player.isOnline ||
                questData.state != QuestState.DOING ||
                innerData.state != QuestState.DOING) {
                cancel(); return@submit
            }
            if (!pass && runTaskPass(player)) {
                schedule++
                if (modeType == ModeType.PERSONAL) {
                    RewardManager.finishReward(player, questData, innerData, this@TargetData)
                    QuestManager.finishInnerQuest(player, questData, innerData)
                    cancel()
                    return@submit
                }
                pass = true
            }else if (!runTaskPass(player)) {
                schedule--
                pass = false
                return@submit
            }
            if (modeType == ModeType.COLLABORATION) {
                val teamData = questData.teamData?: run {
                    cancel()
                    return@submit
                }
                if (runTaskModePass(questData, teamData.members)) {
                    teamData.playerMembers().forEach {
                        RewardManager.finishReward(it, questData, innerData, this@TargetData)
                        QuestManager.finishInnerQuest(it, questData, innerData)
                    }
                }
            }
        }
    }

    private fun runTaskModePass(questData: QuestData, members: MutableSet<UUID>): Boolean {
        var i = 0
        members.forEach {
            val pData = DataStorage.getPlayerData(it)
            val qData = pData.questDataList[questData.questUUID]?: return false
            val tData = qData.questInnerData.targetsData[name]?: return false
            if (tData.schedule >= 1) i++
        }
        return members.size <= i
    }

    private fun runTaskPass(player: Player): Boolean {
        val c = questTarget.conditions
        if (c.isEmpty()) return false
        c.forEach {
            try {
                if (!runEval(player, it)) return false
            } catch (ex: Exception) {
                return false
            }
        }
        return true
    }

}