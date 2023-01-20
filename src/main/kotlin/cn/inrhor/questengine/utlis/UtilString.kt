package cn.inrhor.questengine.utlis

import cn.inrhor.questengine.QuestEngine
import org.bukkit.entity.Player
import taboolib.common.util.VariableReader
import taboolib.common.util.addSafely
import taboolib.module.chat.colored
import taboolib.platform.util.asLangText

object UtilString {

    fun updateLang(): List<String> = QuestEngine.config.getStringList("update.lang")

    val pluginTag by lazy {
        "§7[ §ci §7]§7[ §3QuestEngine §7]"
    }
}

fun String.newLineList(color: String = ""): MutableList<String> {
    val l = this.split("\n").toMutableList()
    l.remove("")
    if (color.isNotEmpty()) {
        val m = mutableListOf<String>()
        l.forEach {
            m.add("$color$it")
        }
        return m
    }
    return l
}

fun String.removeAt(int: Int): String {
    val l = this.newLineList()
    l.removeAt(int)
    return l.joinToString("\n")
}

fun String.indexAdd(index: Int, string: String): String {
    val list = newLineList()
    list.addSafely(index, string, "")
    return list.joinToString("\n")
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

fun String.variableReader(star: String = "{{", end: String = "}}"): MutableList<String> {
    val list = mutableListOf<String>()
    VariableReader(star, end).readToFlatten(this).forEach {
        if (it.isVariable) list.add(it.text)
    }
    return list
}

fun String.spaceSplit(index: Int): String {
    return this.split(" ")[index]
}

fun Boolean.lang(player: Player): String = if (this) player.asLangText("OPEN-ON") else player.asLangText("OPEN-OFF")

fun String.replaceWithOrder(vararg args: Any): String {
    if (args.isEmpty() || isEmpty()) {
        return this
    }
    val chars = toCharArray()
    val builder = StringBuilder(length)
    var i = 0
    while (i < chars.size) {
        val mark = i
        if (chars[i] == '<') {
            var num = 0
            val alias = StringBuilder()
            while (i + 1 < chars.size && chars[i + 1] != '>') {
                i++
                if (Character.isDigit(chars[i]) && alias.isEmpty()) {
                    num *= 10
                    num += chars[i] - '0'
                } else {
                    alias.append(chars[i])
                }
            }
            if (i != mark && i + 1 < chars.size && chars[i + 1] == '>') {
                i++
                if (alias.isNotEmpty()) {
                    val str = alias.toString()
                    builder.append((args.firstOrNull { it is Pair<*, *> && it.second == str } as? Pair<*, *>)?.first ?: "<$str>")
                } else {
                    builder.append(args.getOrNull(num) ?: "<$num>")
                }
            } else {
                i = mark
            }
        }
        if (mark == i) {
            builder.append(chars[i])
        }
        i++
    }
    return builder.toString()
}

/**
 * 对string进行换行，每行最多50个字符
 */
fun String.lineSplit(list: MutableList<String> = mutableListOf(), max: Int = 40): MutableList<String> {
    val line = length/max
    for (i in 0..line) {
        val start = i*max
        val end = (i+1)*max
        if (end > length) {
            list.add("§f"+substring(start, length))
        }else {
            list.add("§f"+substring(start, end))
        }
    }
    return list
}