package cn.inrhor.questengine.utlis.time

import cn.inrhor.questengine.common.quest.QuestState
import org.bukkit.entity.Player
import taboolib.common.platform.console
import taboolib.module.lang.asLangText
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

/**
 * 增加日期时间
 */
fun Date.add(timeUnit: Int, add: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(timeUnit, add)
    return calendar.time
}

/**
 * 时间单位
 *
 * targetData.timeUnit -> timeUnit
 */
fun String.toTimeUnitLang(): String {
    when (this.lowercase()) {
        "s" -> return console().asLangText("QUEST-TIME_S")
        "minute" -> return console().asLangText("QUEST-TIME_MINUTE")
    }
    return ""
}

/**
 * 获得时间标识符
 */
fun String.toTimeUnit(): String {
    val str = this.lowercase()
    if (str == "always") {
        return ""
    }
    return str.split(" ")[0]
}


object TimeUtil {

    fun remainDate(player: Player, state: QuestState, future: Date): String {
        if (state == QuestState.FAILURE) return player.asLangText("QUEST-STATE_FAILURE")
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