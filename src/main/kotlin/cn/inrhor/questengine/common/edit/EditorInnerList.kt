package cn.inrhor.questengine.common.edit

import cn.inrhor.questengine.api.quest.module.inner.QuestInnerModule
import org.bukkit.entity.Player
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

class EditorInnerList(player: Player, header: String, json: TellrawJson = TellrawJson()) : EditorListModule(player, header, json) {

    override fun listAppend(content: String, split: Boolean, index: Int, list: List<*>, button: Array<out EditorButton>) {
        val l: MutableList<QuestInnerModule> = list.toMutableList() as MutableList<QuestInnerModule>
        val get = l[index]
        json.append("      "+get(content, get))
        var sum = 0
        button.forEach {
            val bl = if (split && (sum%2 == 0)) "  " else " "
            sum++
            json.append(bl+get(it.content, get))
            if (it.hover.isNotEmpty()) json.hoverText(get(it.hover, get))
            if (it.command.isNotEmpty()) json.runCommand(it.command)
        }
    }

    fun get(node: String, inner: QuestInnerModule): String {
        return player.asLangText(node, inner.id, inner.name)
    }
}