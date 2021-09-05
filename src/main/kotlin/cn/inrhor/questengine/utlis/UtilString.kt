package cn.inrhor.questengine.utlis

import cn.inrhor.questengine.QuestEngine
import com.google.common.base.Strings
import taboolib.module.chat.colored
import java.util.*

object UtilString {

    fun updateLang(): MutableList<String> = QuestEngine.config.getStringList("update.lang")

    val pluginTag by lazy {
        "§7§l[ §c§li §7§l]§7§l[ §3§lQuestEngine §7§l]"
    }
}

/**
 * 截取特殊字符之后的字符串
 */
fun String.subAfter(meta: String): String {
    return this.substring(this.indexOf(meta)+1)
}

/**
 * 截取特殊字符之前的字符串
 */
fun String.subBefore(meta: String): String {
    return this.substring(0, this.indexOf(meta))
}

/**
 * 百分比状态
 * @param current
 * @param max
 * @param totalBars
 * @param symbol
 * @param completedColor
 * @param notCompletedColor
 * @return
 */
fun progressBar(
    current: Int, max: Int, totalBars: Int, symbol:
    String, completedColor: String,
    notCompletedColor: String): String {
    val percent = current.toFloat() / max
    val progressBars = (totalBars * percent).toInt()
    return (Strings.repeat("" + completedColor + symbol, progressBars)
            + Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars))
}

fun MutableList<String>.toJsonStr(): String {
    var content = ""
    this.forEach {
        if (content.isEmpty()) {
            content = it
            return@forEach
        }
        content = "$content§r\n$it"
    }
    return content.colored()
}