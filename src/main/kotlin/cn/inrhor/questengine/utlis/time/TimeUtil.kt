package cn.inrhor.questengine.utlis.time

import cn.inrhor.questengine.common.quest.enum.StateType
import org.bukkit.entity.Player
import taboolib.common.platform.function.*
import taboolib.module.lang.asLangText
import taboolib.platform.util.asLangText
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar
import java.util.regex.Pattern

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

/**
 * 判断日期YYMMDD避免给定错误日期
 */
fun String.checkDate(strYearMM: String): String {
    val rep =
        "((\\d{2}(([02468][048])|" +
                "([13579][26]))[\\-]((((0?[13578])|" +
                "(1[02]))[\\-]((0?[1-9])|([1-2][0-9])|(3[01])))|" +
                "(((0?[469])|(11))[\\-]((0?[1-9])|([1-2][0-9])|" +
                "(30)))|(0?2[\\-]((0?[1-9])|([1-2][0-9])))))|" +
                "(\\d{2}(([02468][1235679])|([13579][01345789]))" +
                "[\\-]((((0?[13578])|(1[02]))[\\-]((0?[1-9])|([1-2][0-9])|" +
                "(3[01])))|(((0?[469])|(11))[\\-]((0?[1-9])|([1-2][0-9])|" +
                "(30)))|(0?2[\\-]((0?[1-9])|(1[0-9])|(2[0-8]))))))"
    val m = Pattern.matches(rep, this)
    return if (m) this else "$strYearMM-01"
}

fun Date.add(timeUnit: String, add: Int): Date {
    return when (timeUnit) {
        "year" -> this.add(Calendar.YEAR, add)
        "day" -> this.add(Calendar.DATE, add)
        "minute" -> this.add(Calendar.MINUTE, add)
        "s" -> this.add(Calendar.SECOND, add)
        else -> this
    }
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