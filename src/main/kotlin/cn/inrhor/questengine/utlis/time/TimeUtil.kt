package cn.inrhor.questengine.utlis.time

import cn.inrhor.questengine.common.quest.enum.StateType
import org.bukkit.entity.Player
import taboolib.platform.util.asLangText
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar

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
 * 增加日期时间
 */
fun Date.add(timeUnit: Int, add: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(timeUnit, add)
    return calendar.time
}

object TimeUtil {

    fun remainDate(player: Player, state: StateType, future: Date): String {
        if (state == StateType.FAILURE) return player.asLangText("QUEST-STATE_FAILURE")
        val nowDate = Date()
        val i = future.time - nowDate.time
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

}