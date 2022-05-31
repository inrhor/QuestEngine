package cn.inrhor.questengine.utlis

import cn.inrhor.questengine.QuestEngine
import org.bukkit.entity.Player
import taboolib.common.util.VariableReader
import taboolib.module.chat.colored
import taboolib.platform.util.asLangText

object UtilString {

    fun updateLang(): List<String> = QuestEngine.config.getStringList("update.lang")

    val pluginTag by lazy {
        "§7§l[ §c§li §7§l]§7§l[ §3§lQuestEngine §7§l]"
    }
}

fun String.newLineList(): MutableList<String> {
    val l = this.split("\n").toMutableList()
    l.remove("")
    return l
}

fun String.removeAt(int: Int): String {
    val l = this.newLineList()
    l.removeAt(int)
    return l.joinToString("\n")
}

/**
 * 截取特殊字符之后的字符串
 */
fun String.subAfter(meta: String): String {
    return this.substring(this.indexOf(meta)+1)
}


fun List<String>.toJsonStr(): String {
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

fun MutableList<String>.copy(): MutableList<String> {
    val list = mutableListOf<String>()
    this.forEach {
        list.add(it)
    }
    return list
}

fun String.variableReader(): MutableList<String> {
    val list = mutableListOf<String>()
    VariableReader().readToFlatten(this).forEach {
        if (it.isVariable) list.add(it.text)
    }
    return list
}

fun String.spaceSplit(index: Int): String {
    return this.split(" ")[index]
}

fun Boolean.lang(player: Player): String = if (this) player.asLangText("OPEN-ON") else player.asLangText("OPEN-OFF")
