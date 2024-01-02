package cn.inrhor.questengine.api.target.util

import cn.inrhor.questengine.api.event.data.TargetDataEvent
import cn.inrhor.questengine.api.event.TargetEvent
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestMode
import cn.inrhor.questengine.common.quest.manager.TargetManager
import org.bukkit.entity.Player

/**
 * 进度工具
 */
object Schedule {

    /**
     * 进度计数结算与触发奖励
     */
    fun run(player: Player, targetData: TargetData, amount: Int,addProgress: Int = 1) {
        if (targetData.schedule < amount) {
            TargetDataEvent.AddProgress(player, targetData, addProgress).call()
        }
        val allSchedule = TargetManager.scheduleUtil(player, targetData)
        if (allSchedule >= amount) {
            TargetDataEvent.SetProgress(player, targetData, amount).call()
            TargetEvent.Finish(player, targetData, targetData.questID.getQuestMode()).call()
        }
    }

    /**
     * 目标计数meta数量，并触发进度结算
     */
    fun isNumber(player: Player, meta: String, targetData: TargetData) {
        val con = targetData.getTargetFrame()?.nodeMeta(meta, "1")?: return
        run(player, targetData, con[0].toInt())
    }

}