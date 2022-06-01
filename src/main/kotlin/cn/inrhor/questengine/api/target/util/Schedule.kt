package cn.inrhor.questengine.api.target.util

import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.manager.RewardManager
import cn.inrhor.questengine.common.quest.manager.TargetManager
import org.bukkit.entity.Player

/**
 * 进度工具
 */
object Schedule {

    /**
     * 进度计数与触发奖励
     */
    fun run(player: Player , name: String,
            targetData: TargetData, amount: Int): Boolean {
        if (targetData.schedule < amount) {
            targetData.schedule++
        }
        val allSchedule = TargetManager.scheduleUtil(player, name, targetData)
        return RewardManager.finishReward(player, targetData, amount, allSchedule)
    }

    /**
     * 目标计数meta数量
     */
    fun isNumber(player: Player, name: String, meta: String) {
        QuestManager.getDoingTargets(player, name).forEach {
            val target = it.questTarget
            val con = target.nodeMeta(meta)?: return
            run(player, name, it, con[0].toInt())
        }
    }

}