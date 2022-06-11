package cn.inrhor.questengine.common.editor.list

import cn.inrhor.questengine.api.quest.QuestFrame
import org.bukkit.entity.Player
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

class EditorQuestList(player: Player, header: String, json: TellrawJson = TellrawJson()) : EditorListModule(player, header, json) {

    override fun listAppend(content: String, split: Boolean, index: Int, list: List<*>, button: Array<out EditorButton>) {
        if (list.isEmpty()) return
        val l: MutableList<QuestFrame> = list.toMutableList() as MutableList<QuestFrame>
        val get = l[index]
        json.append("      "+get(content, get))
        var sum = 0
        button.forEach {
            val bl = if (split && (sum%2 == 0)) "  " else " "
            sum++
            json.append(bl+get(it.content, get))
            if (it.hover.isNotEmpty()) json.hoverText(get(it.hover, get))
            if (it.command.isNotEmpty()) json.runCommand(it.command.replace("{questID}", get.id))
        }
    }

    fun get(node: String, quest: QuestFrame): String {
        return player.asLangText(node, quest.id, quest.name)
    }
}