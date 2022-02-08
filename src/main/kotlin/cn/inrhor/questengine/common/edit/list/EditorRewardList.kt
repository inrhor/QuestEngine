package cn.inrhor.questengine.common.edit.list

import cn.inrhor.questengine.api.quest.module.inner.FinishReward
import org.bukkit.entity.Player
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

class EditorRewardList(player: Player, header: String, json: TellrawJson = TellrawJson()) : EditorListModule(player, header, json) {

    override fun listAppend(content: String, split: Boolean, index: Int, list: List<*>, button: Array<out EditorButton>) {
        if (list.isEmpty()) return
        val l: MutableList<FinishReward> = list.toMutableList() as MutableList<FinishReward>
        val get = l[index]
        json.append("      "+get(content, get))
        var sum = 0
        button.forEach {
            val bl = if (split && (sum%2 == 0)) "  " else " "
            sum++
            json.append(bl+get(it.content, get))
            if (it.hover.isNotEmpty()) json.hoverText(get(it.hover, get))
            if (it.command.isNotEmpty()) json.runCommand(
                it.command.replace("[0]", index.toString()).replace("[1]", get.id))
        }
    }

    fun get(node: String, finishReward: FinishReward): String {
        return player.asLangText(node, finishReward.id)
    }
}