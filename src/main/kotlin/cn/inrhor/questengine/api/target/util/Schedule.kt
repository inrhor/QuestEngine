package cn.inrhor.questengine.api.target.util

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
    fun run(player: Player , name: String, targetData: TargetData, amount: Int) {
        if (targetData.schedule < amount) {
            targetData.schedule++
        }
        val allSchedule = TargetManager.scheduleUtil(player, name, targetData)
        if (allSchedule >= amount) {
            TargetEvent.Finish(player, targetData, targetData.questID.getQuestMode()).call()
        }
    }

    /**
     * 目标计数meta数量，并触发进度结算
     */
    fun isNumber(player: Player, name: String, meta: String, targetData: TargetData) {
        val con = targetData.getTargetFrame().nodeMeta(meta)?: return
        run(player, name, targetData, con[0].toInt())
    }

}