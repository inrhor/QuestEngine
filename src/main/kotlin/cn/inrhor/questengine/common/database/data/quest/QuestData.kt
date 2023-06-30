package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.quest.manager.QuestManager.acceptQuest
import cn.inrhor.questengine.common.quest.manager.QuestManager.failQuest
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestFrame
import cn.inrhor.questengine.common.record.QuestRecord
import cn.inrhor.questengine.utlis.time.noTimeout
import cn.inrhor.questengine.utlis.time.toDate
import cn.inrhor.questengine.utlis.time.toStr
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import java.util.*

data class QuestData(
    override val id: String = "?",
    var target: MutableList<TargetData> = mutableListOf(),
    var state: StateType = StateType.DOING, var time: String = Date().toStr(), var end: String =""): QuestRecord.ActionFunc {

    constructor(questFrame: QuestFrame): this(questFrame.id, questFrame.newTargetsData())

    /**
     * 更新时间, 支持周期时间
     * 加载数据使用此方法
     */
    fun updateTime(player: Player) {
        val timeAddon = id.getQuestFrame()?.time?: return
        val timeDate = timeAddon.timeDate
        val endDate = timeAddon.endDate
        if (endDate != null) {
            // 任务开始时间
            val start = time.toDate()
            if (state == StateType.DOING) {
                // 如果现在时间或任务开始时间不在任务时间段内
                if (!Date().noTimeout(timeDate, endDate) || !start.noTimeout(timeDate, endDate)) {
                    player.failQuest(id)
                }
            } else if (state == StateType.FAILURE || state == StateType.FINISH) {
                // 如果不重置任务
                if (!timeAddon.reset) return
                // 如果任务开始时间不在任务时间段内
                if (!start.noTimeout(timeDate, endDate)) {
                    // 如果现在时间在任务时间段内
                    if (Date().noTimeout(timeDate, endDate)) {
                        player.acceptQuest(id)
                        return
                    }
                }
            }
            submit(delay = 20L, async = true) {
                if (player.isOnline) {
                    submit {
                        updateTime(player)
                    }
                }
            }
        }
    }

    /**
     * @return 是否完成了所有目标
     */
    fun isFinishTarget(): Boolean {
        var finish = 0
        val targetSize = target.size
        target.forEach {
            if (it.state == StateType.FINISH) finish++
        }
        return finish >= targetSize
    }

}