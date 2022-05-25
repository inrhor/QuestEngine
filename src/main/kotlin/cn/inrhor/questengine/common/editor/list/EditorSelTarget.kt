package cn.inrhor.questengine.common.editor.list

import org.bukkit.entity.Player
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

class EditorSelTarget(player: Player, header: String, json: TellrawJson = TellrawJson()) : EditorListModule(player, header, json) {

    override fun listAppend(content: String, split: Boolean, index: Int, list: List<*>, button: Array<out EditorButton>) {
        if (list.isEmpty()) return
        val l: MutableList<String> = list.toMutableList() as MutableList<String>
        val get = l[index]
        val g = get.replace(" ", "-").uppercase()
        val name = if (get =="TASK") "task xxx_${System.currentTimeMillis()}" else get
        json.append("      "+player.asLangText("$content-$g", name))
        button.forEach {
            json.append("  "+player.asLangText(it.content))
            if (it.hover.isNotEmpty()) json.hoverText(player.asLangText(it.hover))
            json.runCommand(it.command.replace("{targetName}", "'$name'")).newLine()
        }
    }
}