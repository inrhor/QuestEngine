package cn.inrhor.questengine.common.edit.list

import cn.inrhor.questengine.script.kether.runEval
import org.bukkit.entity.Player
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

class EditorOfList(player: Player, header: String, json: TellrawJson = TellrawJson()) : EditorListModule(player, header, json) {

    override fun listAppend(content: String, split: Boolean, index: Int, list: List<*>, button: Array<out EditorButton>) {
        if (list.isEmpty()) return
        val l: MutableList<String> = list.toMutableList() as MutableList<String>
        val get = l[index]
        json.append("      "+player.asLangText(content, get))
        var sum = 0
        button.forEach {
            val bl = if (split && (sum%2 == 0)) "        " else " "
            if (it.content.contains("-CONDITION-RETURN")) {
                json.newLine().append("        "+player.asLangText(it.content))
                if (runEval(player, get)) {
                    json.append(" "+player.asLangText(it.content+"-TRUE"))
                }else json.append(" "+player.asLangText(it.content+"-FALSE"))
                json.newLine()
            }else if (it.content.contains("-SCRIPT-RETURN")) {

            }else {
                sum++
                json.append(bl+player.asLangText(it.content))
            }
            if (it.hover.isNotEmpty()) json.hoverText(player.asLangText(it.hover))
            if (it.command.isNotEmpty()) json.runCommand(it.command+" $index")
        }
    }
}