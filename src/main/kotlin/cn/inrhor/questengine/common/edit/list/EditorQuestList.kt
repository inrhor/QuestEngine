package cn.inrhor.questengine.common.edit.list

import cn.inrhor.questengine.api.quest.module.main.QuestModule
import org.bukkit.entity.Player
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

class EditorQuestList(player: Player, header: String, json: TellrawJson = TellrawJson()) : EditorListModule(player, header, json) {

    override fun listAppend(content: String, split: Boolean, index: Int, list: List<*>, button: Array<out EditorButton>) {
        if (list.isEmpty()) return
        val l: MutableList<QuestModule> = list.toMutableList() as MutableList<QuestModule>
        val get = l[index]
        json.append("      "+get(content, get))
        var sum = 0
        button.forEach {
            val bl = if (split && (sum%2 == 0)) "  " else " "
            sum++
            json.append(bl+get(it.content, get))
            if (it.hover.isNotEmpty()) json.hoverText(get(it.hover, get))
            if (it.command.isNotEmpty()) json.runCommand(it.command.replace("{questID}", get.questID))
        }
    }

    fun get(node: String, quest: QuestModule): String {
        return player.asLangText(node, quest.questID, quest.name)
    }
}