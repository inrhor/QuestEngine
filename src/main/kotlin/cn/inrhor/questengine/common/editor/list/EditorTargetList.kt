package cn.inrhor.questengine.common.editor.list

import cn.inrhor.questengine.api.quest.TargetFrame
import org.bukkit.entity.Player
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

class EditorTargetList(player: Player, header: String, json: TellrawJson = TellrawJson()) : EditorListModule(player, header, json) {

    override fun listAppend(content: String, split: Boolean, index: Int, list: List<*>, button: Array<out EditorButton>) {
        if (list.isEmpty()) return
        val l: MutableList<TargetFrame> = list.toMutableList() as MutableList<TargetFrame>
        val get = l[index]
        json.append("      "+get(content, get))
        var sum = 0
        button.forEach {
            val bl = if (split && (sum%2 == 0)) "  " else " "
            sum++
            json.append(bl+get(it.content, get))
            if (it.hover.isNotEmpty()) json.hoverText(get(it.hover, get))
            if (it.command.isNotEmpty()) json.runCommand(
                it.command.replace("{targetID}", get.id))
        }
    }

    fun get(node: String, target: TargetFrame): String {
        return player.asLangText(node, target.id, target.event)
    }
}