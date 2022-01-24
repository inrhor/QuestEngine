package cn.inrhor.questengine.common.edit

import org.bukkit.entity.Player
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

open class EditorListModule(val player: Player, header: String, val json: TellrawJson = TellrawJson()) {

    init {
        json.newLine().append("   $header").newLine()
    }

    fun add(content: String, vararg button: EditorButton): EditorListModule {
        json.append("      $content")
        button.forEach {
            json.append("  "+it.content)
            if (it.hover.isNotEmpty()) json.hoverText(it.hover)
            if (it.command.isNotEmpty()) json.runCommand(it.command)
        }
        json.newLine()
        return this
    }

    open fun listAppend(content: String, split: Boolean, index: Int, list: List<*>, button: Array<out EditorButton>) {}

    fun list(page: Int, limit: Int, list: List<*>, split: Boolean, content: String, preNextCmd: String, vararg button: EditorButton): EditorListModule {
        val a = page*limit
        val listSize = list.size
        val b = if (a < listSize) a else listSize-1
        for ((m, i) in (b until listSize).withIndex()) {
            if (m >= limit) {
                break
            }
            json.newLine()
            listAppend(content, split, i, list, button)
            /*button.forEach {
                buttonBuild(it)
            }*/
        }
        if (page > 0) {
            json
                .newLine()
                .append("   "+player.asLangText("EDITOR-PREVIOUS-PAGE"))
                .hoverText(player.asLangText("EDITOR-PREVIOUS-PAGE-HOVER"))
                .runCommand("/$preNextCmd "+(page-1))
        }
        if ((page+1)*limit <= listSize-1) {
            json
                .newLine()
                .append("   "+player.asLangText("EDITOR-NEXT-PAGE"))
                .hoverText(player.asLangText("EDITOR-NEXT-PAGE-HOVER"))
                .runCommand("/$preNextCmd "+(page+1))
        }
        json.newLine()
        return this
    }

    class EditorButton(val content: String, val hover: String = "", val command: String = "")

}