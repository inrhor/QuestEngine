package cn.inrhor.questengine.utlis.time

import cn.inrhor.questengine.common.quest.*
import cn.inrhor.questengine.common.quest.enum.StateType
import org.bukkit.entity.Player
import taboolib.common5.TimeCycle
import taboolib.platform.util.asLangText
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


fun Date.toStr(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return dateFormat.format(this)
}

fun String.toDate(): Date {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return dateFormat.parse(this)
}

fun Date.toStrYearMM(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM")
    return dateFormat.format(this)
}

/**
 * @return 是否没超时
 */
fun Date.noTimeout(before: Date, after: Date): Boolean {
    return after(before) && before(after)
}

/**
 * 增加日期时间
 */
fun Date.add(timeUnit: Int, add: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(timeUnit, add)
    return calendar.time
}

fun Date.remainDate(player: Player, state: StateType): String {
    if (state == StateType.FAILURE) return player.asLangText("STATE-TYPE-FAILURE")
    val nowDate = Date()
    val i = time - nowDate.time
    val day = i / (24 * 60 * 60 * 1000)
    if (day < 0) return player.asLangText("QUEST-TIMEOUT")
    val hour = i / (60 * 60 * 1000) - day * 24
    if (hour < 0) return player.asLangText("QUEST-TIMEOUT")
    val minute = i / (60 * 1000) - day * 24 * 60 - hour * 60
    if (minute < 0) return player.asLangText("QUEST-TIMEOUT")
    val second = i / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60
    if (second < 0) return player.asLangText("QUEST-TIMEOUT")
    val d = player.asLangText("QUEST-TIME_DAY")
    val h = player.asLangText("QUEST-TIME_HOUR")
    val m = player.asLangText("QUEST-TIME_MINUTE")
    val s = player.asLangText("QUEST-TIME_S")
    return "$day$d$hour$h$minute$m$second$s"
}

fun TimeCycle.remaining(player: Player): String {
    val currentTime = System.currentTimeMillis()

    val timeRemainingMillis = if (type === TimeCycle.Type.TIME) {
        time - (currentTime - time)
    } else {
        end.timeInMillis - currentTime
    }

    if (timeRemainingMillis <= 0) {
        return player.asLangText("COOL_DOWN_OK")
    }

    val days = TimeUnit.MILLISECONDS.toDays(timeRemainingMillis)
    val hours = TimeUnit.MILLISECONDS.toHours(timeRemainingMillis) % 24
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemainingMillis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemainingMillis) % 60

    return player.asLangText("COOL_DOWN", days, hours, minutes, seconds)
}