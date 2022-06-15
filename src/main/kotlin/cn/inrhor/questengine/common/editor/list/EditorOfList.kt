package cn.inrhor.questengine.common.editor.list

import cn.inrhor.questengine.api.target.RegisterTarget
import cn.inrhor.questengine.script.kether.*
import org.bukkit.entity.Player
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored
import taboolib.platform.util.asLangText

class EditorOfList(player: Player, header: String, json: TellrawJson = TellrawJson(), val empty: String = "        ", vararg val other: String) : EditorListModule(player, header, json) {

    override fun listAppend(content: String, split: Boolean, index: Int, list: List<*>, button: Array<out EditorButton>) {
        if (list.isEmpty()) return
        val l: MutableList<String> = list.toMutableList() as MutableList<String>
        val get = l[index]
        json.append("      "+player.asLangText(content, get))
        if (content=="EDITOR-CONTENT-LIST") json.newLine()
        var sum = 0
        button.forEach {
            val bl = if (split && (sum%2 == 0)) empty else " "
            if (it.content.contains("-CONDITION-RETURN")) {
                json.newLine().append("        "+player.asLangText(it.content))
                val type = if (content == "EDITOR-TARGET-LIST-FOR-NODE") backContains(player, content,
                    RegisterTarget.getNode(other[0], other[1])?.contains(get, player)?: true
                , false) else backContains(player, get)
                json.append(" "+type.lang(player, it.content))
                if (type == EvalType.ERROR) {
                    json.hoverText("&7".colored()+ feedbackEval(player, get))
                }
                json.newLine()
            }else if (it.content.contains("-SCRIPT-RETURN")) {
                json.newLine().append("        "+player.asLangText(it.content))
                val type = testEval(player, get)
                if (type != EvalType.ERROR) {
                    json.append(" "+player.asLangText(it.content+"-TRUE"))
                }else {
                    json
                        .append(" "+type.lang(player, it.content))
                        .hoverText("&7".colored()+ feedbackEval(player, get))
                }
                json.newLine()
            } else {
                sum++
                if (it.content == "EDITOR-LIST-INNER-NOTE-ADD") json.newLine().append("      ")
                json.append(bl + player.asLangText(it.content))
            }
            if (it.hover.isNotEmpty()) json.hoverText(player.asLangText(it.hover))
            if (it.command.isNotEmpty()) json.runCommand(
                it.command.replace("{index}", index.toString()))
            if (it.content.contains("EDITOR-LIST-DEL-META")) json.newLine()
        }
    }
}