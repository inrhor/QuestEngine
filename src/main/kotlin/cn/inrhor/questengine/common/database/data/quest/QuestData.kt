package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.api.quest.TimeAddon
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.quest.manager.QuestManager.acceptQuest
import cn.inrhor.questengine.common.quest.manager.QuestManager.failQuest
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestFrame
import cn.inrhor.questengine.common.record.QuestRecord
import cn.inrhor.questengine.utlis.time.noTimeout
import cn.inrhor.questengine.utlis.time.toDate
import cn.inrhor.questengine.utlis.time.toStr
import com.avaje.ebeaninternal.server.core.BasicTypeConverter.toDate
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import java.util.*

data class QuestData(
    override val id: String = "?",
    var target: MutableList<TargetData> = mutableListOf(),
    var state: StateType = StateType.DOING, var time: String = Date().toStr(), var end: String =""): QuestRecord.ActionFunc {

    constructor(questFrame: QuestFrame): this(questFrame.id, questFrame.newTargetsData())

    /**
     * 接受任务时为 CUSTOM 生成独立时间
     */
    fun generateTime() {
        val timeAddon = id.getQuestFrame()?.time?: return
        if (timeAddon.type != TimeAddon.Type.CUSTOM) return
        // 开始时间
        time = Date().toStr()
        // 终止时间
        val add = timeAddon.duration.lowercase().split(" ")
        val cal = Calendar.getInstance()
        val t = add[1].toInt()
        when (add[0]) {
            "s" -> {
                cal.add(Calendar.SECOND, t)
            }
            "m" -> {
                cal.add(Calendar.MINUTE, t)
            }
            "h" -> {
                cal.add(Calendar.HOUR, t)
            }
        }
        end = cal.time?.toStr()?: ""
    }

    @Transient private var taskTime: PlatformExecutor.PlatformTask? = null

    /**
     * 注销数据
     */
    fun unload() {
        taskTime?.cancel()
        taskTime = null
    }

    /**
     * 更新时间, 支持周期时间
     * 加载数据使用此方法
     */
    fun updateTime(player: Player) {
        val timeAddon = id.getQuestFrame()?.time?: return
        val timeDate = if (timeAddon.type == TimeAddon.Type.CUSTOM) {
            time.toDate()
        }else {
            timeAddon.timeDate
        }
        val endDate = if (timeAddon.type == TimeAddon.Type.CUSTOM) {
            end.toDate()
        }else {
            timeAddon.endDate
        }?: return
        // 任务开始时间
        val start = time.toDate()
        taskTime = submit(period = 20L, async = true) {
            if (!player.isOnline) {
                unload()
                return@submit
            }
            if (state != StateType.NOT_ACCEPT) {
                when (state) {
                    StateType.DOING -> {
                        // 如果现在时间不在任务时间段内
                        if (!Date().noTimeout(timeDate, endDate)) {
                            player.failQuest(id)
                        }
                    }
                    StateType.FINISH, StateType.FAILURE -> {
                        // 如果重置任务
                        if (timeAddon.reset) {
                            // 如果任务开始时间不在任务时间段内
                            if (!start.noTimeout(timeDate, endDate)) {
                                // 如果现在时间在任务时间段内
                                if (Date().noTimeout(timeDate, endDate)) {
                                    player.acceptQuest(id)
                                    return@submit
                                }
                            }
                        }
                    }
                    else -> {}
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