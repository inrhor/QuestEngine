package cn.inrhor.questengine.common.edit.list

import org.bukkit.entity.Player
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

class EditorOfList(player: Player, header: String, json: TellrawJson = TellrawJson()) : EditorListModule(player, header, json) {

    override fun listAppend(content: String, split: Boolean, index: Int, list: List<*>, button: Array<out EditorButton>) {
        val l: MutableList<String> = list.toMutableList() as MutableList<String>
        val get = l[index]
        json.append("      "+player.asLangText(content, get))
        var sum = 0
        button.forEach {
            val bl = if (split && (sum%2 == 0)) "  " else " "
            sum++
            json.append(bl+player.asLangText(it.content))
            if (it.hover.isNotEmpty()) json.hoverText(player.asLangText(it.hover))
            if (it.command.isNotEmpty()) json.runCommand(it.command+" $index")
        }
    }
}