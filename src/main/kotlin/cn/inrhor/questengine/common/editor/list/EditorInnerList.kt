package cn.inrhor.questengine.common.editor.list

import cn.inrhor.questengine.api.quest.module.inner.QuestInnerModule
import cn.inrhor.questengine.api.quest.module.main.QuestModule
import org.bukkit.entity.Player
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

class EditorInnerList(player: Player, val questModule: QuestModule, header: String, json: TellrawJson = TellrawJson()) : EditorListModule(player, header, json) {

    override fun listAppend(content: String, split: Boolean, index: Int, list: List<*>, button: Array<out EditorButton>) {
        if (list.isEmpty()) return
        val l: MutableList<QuestInnerModule> = list.toMutableList() as MutableList<QuestInnerModule>
        val get = l[index]
        json.append("      "+get(content, get))
        var sum = 0
        button.forEach {
            val bl = if (split && (sum%2 == 0)) "  " else " "
            sum++
            val command = it.command.replace("{innerID}", get.id)
            if (it.content == "EDITOR-EDIT-QUEST-START-STATE-META") {
                if (questModule.startInnerQuestID == get.id) {
                    json.append(bl + player.asLangText(it.content+"_2"))
                }else {
                    json
                        .append(bl + player.asLangText(it.content+"_1"))
                        .hoverText(get(it.hover, get))
                        .runCommand(command)
                }
            }else {
                json.append(bl + get(it.content, get))
                if (it.hover.isNotEmpty()) json.hoverText(get(it.hover, get))
                if (it.command.isNotEmpty()) {
                    if (it.content == "EDITOR-LIST-INNER-DEL-META" || it.content == "EDITOR-LIST-INNER-EDIT-META") {
                        json.runCommand(command)
                    } else {
                        json.runCommand(command)
                    }
                }
            }
        }
    }

    fun get(node: String, inner: QuestInnerModule): String {
        return player.asLangText(node, inner.id, inner.name, inner.id+"§7(${questModule.questID})")
    }
}