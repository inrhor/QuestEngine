package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.api.quest.module.inner.TimeFrame
import cn.inrhor.questengine.api.quest.module.main.QuestModule
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.teamData
import cn.inrhor.questengine.common.quest.ModeType
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.manager.RewardManager
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import java.text.SimpleDateFormat
import java.util.*

data class QuestInnerData(
    val questID: String,
    val innerQuestID: String,
    var targetsData: MutableMap<String, TargetData>,
    var state: QuestState, var timeDate: Date = Date(), var end: Date? = null) {

    fun stateToggle(player: Player, questData: QuestData, state: QuestState, questModule: QuestModule, reward: Boolean = false, isTrigger: Boolean = false) {
        this.state = state
        info("state $state")
        val questUUID = questData.questUUID
        if (questModule.mode.type == ModeType.COLLABORATION && isTrigger) {
            player.teamData()?.playerMembers()?.forEach {
                val mQuest = QuestManager.getQuestData(it, questUUID)
                val mInner = QuestManager.getInnerQuestData(it, questUUID, innerQuestID)
                if (mQuest != null && mInner != null) {
                    mInner.stateToggle(it, mQuest, state, questModule, true)
                }
            }
        }
        if (reward && state == QuestState.FINISH) {
            RewardManager.sendFinish(player, this)
        }
    }

    fun getTargetData(name: String, finish: Boolean = false): TargetData? {
        targetsData.values.forEach {
            if (it.name == name) {
                if (finish) {
                    if (it.state == QuestState.FINISH) return it
                }else return it
            }
        }
        return null
    }

    fun updateTime(timeFrame: TimeFrame) {
        timeDate = Date()
        val type = timeFrame.type
        val duration = timeFrame.duration
        if (type != TimeFrame.Type.ALWAYS) {
            val sp = duration.split(">")
            val a = sp[0].split(",")
            val b = sp[1].split(",")
            when (type) {
                TimeFrame.Type.DAY -> {
                    val ymdFormat = SimpleDateFormat("yyyy-MM-dd")
                    val ymd= ymdFormat.format(timeDate)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    timeDate = dateFormat.parse("$ymd ${a[0]}")
                    end = dateFormat.parse("$ymd ${b[0]}")
                }
                TimeFrame.Type.ALWAYS -> {}
                TimeFrame.Type.WEEKLY -> {
                    val cal1 = Calendar.getInstance()
                    cal1.set(Calendar.DAY_OF_WEEK, a[0].toInt()) // 当前周某一天，1是上周日，2是本周一
                    val cal2 = Calendar.getInstance()
                    cal2.set(Calendar.DAY_OF_WEEK, b[0].toInt())
                    val c = a[1].split(":") ;val d = b[1].split(":")
                    cal1.set(Calendar.HOUR, c[0].toInt());cal1.set(Calendar.MINUTE, c[1].toInt());cal1.set(Calendar.SECOND, c[2].toInt())
                    cal2.set(Calendar.HOUR, d[0].toInt());cal2.set(Calendar.MINUTE, d[1].toInt());cal2.set(Calendar.SECOND, d[2].toInt())
                    timeDate = cal1.time
                    end = cal2.time
                }
                TimeFrame.Type.MONTHLY -> {
                    val cal1 = Calendar.getInstance()
                    val cal2 = Calendar.getInstance()
                    cal1.set(Calendar.DAY_OF_MONTH, a[0].toInt()) // 当前月的某一天
                    cal2.set(Calendar.DAY_OF_MONTH, b[0].toInt())
                    val c = a[1].split(":") ;val d = b[1].split(":")
                    cal1.set(Calendar.HOUR, c[0].toInt());cal1.set(Calendar.MINUTE, c[1].toInt());cal1.set(Calendar.SECOND, c[2].toInt())
                    cal2.set(Calendar.HOUR, d[0].toInt());cal2.set(Calendar.MINUTE, d[1].toInt());cal2.set(Calendar.SECOND, d[2].toInt())
                    timeDate = cal1.time
                    end = cal2.time
                }
                TimeFrame.Type.YEARLY -> {
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
                    end = cal2.time
                }
                TimeFrame.Type.CUSTOM -> {
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
                    end = cal.time
                }
            }
        }
    }

    /**
     * 是否完成了内部任务的所有目标
      */
    fun isFinishTarget(): Boolean {
        var finish = 0
        val targetSize = targetsData.size
        info("size $targetSize")
        targetsData.values.forEach {
            info("state ${it.state}  id ${it.questTarget.id}")
            if (it.state == QuestState.FINISH) finish++
        }
        info("finish $finish")
        return finish >= targetSize
    }

}