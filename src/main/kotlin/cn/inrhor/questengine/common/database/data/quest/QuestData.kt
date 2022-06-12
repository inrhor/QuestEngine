package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.api.quest.TimeAddon
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.quest.manager.QuestManager.acceptQuest
import cn.inrhor.questengine.common.quest.manager.QuestManager.failQuest
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestFrame
import cn.inrhor.questengine.utlis.time.noTimeout
import cn.inrhor.questengine.utlis.time.toDate
import cn.inrhor.questengine.utlis.time.toStr
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import java.text.SimpleDateFormat
import java.util.*

data class QuestData(
    val id: String = "?",
    var target: MutableList<TargetData> = mutableListOf(),
    var state: StateType = StateType.DOING, var time: String = Date().toStr(), var end: String ="") {

    @Transient var timeDate: Date = Date()
    @Transient var endDate: Date? = null

    constructor(questFrame: QuestFrame): this(questFrame.id, questFrame.newTargetsData())

    /**
     * 更新时间, 支持周期时间
     * 加载数据使用此方法
     */
    fun updateTime(player: Player) {
        val timeAddon = id.getQuestFrame().time
        timeDate = Date()
        val type = timeAddon.type
        val duration = timeAddon.duration
        if (type != TimeAddon.Type.ALWAYS) {
            val sp = duration.split(">")
            val a = sp[0].split(",")
            val b = sp[1].split(",")
            when (type) {
                TimeAddon.Type.DAY -> {
                    val ymdFormat = SimpleDateFormat("yyyy-MM-dd")
                    val ymd= ymdFormat.format(timeDate)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    timeDate = dateFormat.parse("$ymd ${a[0]}")
                    endDate = dateFormat.parse("$ymd ${b[0]}")
                }
                TimeAddon.Type.ALWAYS -> {}
                TimeAddon.Type.WEEKLY -> {
                    val cal1 = Calendar.getInstance()
                    cal1.set(Calendar.DAY_OF_WEEK, a[0].toInt()) // 当前周某一天，1是上周日，2是本周一
                    val cal2 = Calendar.getInstance()
                    cal2.set(Calendar.DAY_OF_WEEK, b[0].toInt())
                    val c = a[1].split(":") ;val d = b[1].split(":")
                    cal1.set(Calendar.HOUR, c[0].toInt());cal1.set(Calendar.MINUTE, c[1].toInt());cal1.set(Calendar.SECOND, c[2].toInt())
                    cal2.set(Calendar.HOUR, d[0].toInt());cal2.set(Calendar.MINUTE, d[1].toInt());cal2.set(Calendar.SECOND, d[2].toInt())
                    timeDate = cal1.time
                    endDate = cal2.time
                }
                TimeAddon.Type.MONTHLY -> {
                    val cal1 = Calendar.getInstance()
                    val cal2 = Calendar.getInstance()
                    cal1.set(Calendar.DAY_OF_MONTH, a[0].toInt()) // 当前月的某一天
                    cal2.set(Calendar.DAY_OF_MONTH, b[0].toInt())
                    val c = a[1].split(":") ;val d = b[1].split(":")
                    cal1.set(Calendar.HOUR, c[0].toInt());cal1.set(Calendar.MINUTE, c[1].toInt());cal1.set(Calendar.SECOND, c[2].toInt())
                    cal2.set(Calendar.HOUR, d[0].toInt());cal2.set(Calendar.MINUTE, d[1].toInt());cal2.set(Calendar.SECOND, d[2].toInt())
                    timeDate = cal1.time
                    endDate = cal2.time
                }
                TimeAddon.Type.YEARLY -> {
                    val cal1 = Calendar.getInstance()
                    cal1.set(Calendar.MONTH, a[0].toInt()) // 当前年某一月，0是一月
                    val cal2 = Calendar.getInstance()
                    cal2.set(Calendar.MONTH, b[0].toInt())
                    cal1.set(Calendar.DAY_OF_MONTH, a[1].toInt()) // 当前月的某一天
                    cal2.set(Calendar.DAY_OF_MONTH, b[1].toInt())
                    val c = a[2].split(":") ;val d = b[2].split(":")
                    cal1.set(Calendar.HOUR, c[0].toInt());cal1.set(Calendar.MINUTE, c[1].toInt());cal1.set(Calendar.SECOND, c[2].toInt())
                    cal2.set(Calendar.HOUR, d[0].toInt());cal2.set(Calendar.MINUTE, d[1].toInt());cal2.set(Calendar.SECOND, d[2].toInt())
                    timeDate = cal1.time
                    endDate = cal2.time
                }
                TimeAddon.Type.CUSTOM -> {
                    val add = duration.lowercase().split(" ")
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
                    endDate = cal.time
                }
            }
            if (endDate != null) {
                // 任务开始时间
                val start = time.toDate()
                // 如果任务开始时间不在任务时间段内
                if (!start.noTimeout(timeDate, endDate!!)) {
                    info("timeout")
                    if (state == StateType.DOING) {
                        info("doing -> fail")
                        player.failQuest(id)
                    }else if (state == StateType.FAILURE || state == StateType.FINISH) {
                        // 如果现在时间在任务时间段内
                        info("fail finish")
                        if (Date().noTimeout(timeDate, endDate!!)) {
                            info("no timeout set to accept")
                            player.acceptQuest(id)
                        }
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