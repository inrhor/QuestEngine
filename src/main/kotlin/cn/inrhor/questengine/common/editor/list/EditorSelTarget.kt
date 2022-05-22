package cn.inrhor.questengine.common.editor.list

import org.bukkit.entity.Player
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

class EditorSelTarget(player: Player, header: String, json: TellrawJson = TellrawJson()) : EditorListModule(player, header, json) {

    override fun listAppend(content: String, split: Boolean, index: Int, list: List<*>, button: Array<out EditorButton>) {
        if (list.isEmpty()) return
        val l: MutableList<String> = list.toMutableList() as MutableList<String>
        val get = l[index]
        val name = if (get.startsWith("TASK ")) "task xxx" else get.replace(" ", "-").uppercase()
        json.append("      "+player.asLangText("$content-$name", get))
        button.forEach {
            json.append("  "+player.asLangText(it.content))
            if (it.hover.isNotEmpty()) json.hoverText(player.asLangText(it.hover))
            json.runCommand(it.command.replace("{targetName}", "'$get'")).newLine()
        }
    }
}