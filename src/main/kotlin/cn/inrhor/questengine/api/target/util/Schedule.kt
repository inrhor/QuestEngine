package cn.inrhor.questengine.api.target.util

import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.manager.RewardManager
import cn.inrhor.questengine.common.quest.manager.TargetManager
import org.bukkit.entity.Player

/**
 * 进度工具
 */
object Schedule {

    /**
     * 进度计数结算与触发奖励
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
     * 目标计数meta数量，并触发进度结算
     */
    fun isNumber(player: Player, name: String, meta: String, targetData: TargetData) {
        val con = targetData.questTarget.nodeMeta(meta)?: return
        run(player, name, targetData, con[0].toInt())
    }

}