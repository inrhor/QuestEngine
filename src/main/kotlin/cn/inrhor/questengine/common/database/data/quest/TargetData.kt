package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.ModeType
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.api.quest.module.QuestTarget
import cn.inrhor.questengine.common.database.data.teamData
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.manager.RewardManager
import cn.inrhor.questengine.script.kether.runEval

import org.bukkit.entity.Player
import taboolib.common.platform.function.*
import java.util.*

/**
 * 任务目标存储
 */
data class TargetData(
    val questUUID: UUID,
    val innerID: String,
    val name: String,
    var schedule: Int, val questTarget: QuestTarget, var state: QuestState = QuestState.DOING) {

    /**
     * 启用调度类型的任务目标
     * 备注：要求多人完成的，则判断 成员数 <= 进度
     */
    fun runTask(player: Player, questData: GroupData, innerData: QuestData, modeType: ModeType) {
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
                    RewardManager.finishReward(player, this@TargetData)
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
                val teamData = player.teamData()?: run {
                    cancel()
                    return@submit
                }
                if (runTaskModePass(questData, teamData.members)) {
                    teamData.playerMembers().forEach {
                        RewardManager.finishReward(it, this@TargetData)
                        QuestManager.finishInnerQuest(it, questData, innerData)
                    }
                }
            }
        }
    }

    private fun runTaskModePass(questData: GroupData, members: MutableSet<UUID>): Boolean {
        var i = 0
        members.forEach {
            val pData = DataStorage.getPlayerData(it)
            val qData = pData.questDataList[questData.uuid]?: return false
            val tData = qData.questInnerData.getTargetData(name)?: return false
            if (tData.schedule >= 1) i++
        }
        return members.size <= i
    }

    private fun runTaskPass(player: Player): Boolean {
        val c = questTarget.condition
        if (c.isEmpty()) return false
        try {
            if (!runEval(player, c)) return false
        } catch (ex: Exception) {
            return false
        }
        return true
    }

}