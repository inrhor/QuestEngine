package cn.inrhor.questengine.common.edit

import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

object EditorList {
    /**
     * 可视化 - 任务列表
     */
    fun Player.editorListQuest() {
        listQuest(this)
    }

    fun listQuest(player: Player, page: Int = 0) {
        val json = TellrawJson().newLine().append("   "+player.asLangText("EDITOR-LIST-QUEST")).newLine()
        val list = QuestManager.questMap.values.toMutableList()
        val a = page*10
        val b = if (a < list.size) a else list.size-1
        for ((limit, i) in (b..list.size).withIndex()) {
            if (limit >= 10) {
                break
            }
            val it = list[i]
            json
                .newLine()
                .append("      "+player.asLangText("EDITOR-LIST-QUEST-INFO", it.questID, it.name))
                .append("   "+player.asLangText("EDITOR-LIST-QUEST-EDIT"))
                .append(" "+player.asLangText("EDITOR-LIST-QUEST-EDIT-META"))
                .hoverText(player.asLangText("EDITOR-LIST-QUEST-EDIT-HOVER", it.questID))
                .runCommand("/qen editor edit quest "+it.questID)
                .newLine()
                .append("  "+player.asLangText("EDITOR-LIST-QUEST-DEL"))
                .append(" "+player.asLangText("EDITOR-LIST-QUEST-DEL-META"))
                .hoverText(player.asLangText("EDITOR-LIST-QUEST-DEL-HOVER", it.questID))
                .runCommand("/qen editor del quest "+it.questID)
                .newLine()
        }
        if (page > 0) {
            json
                .newLine()
                .append(player.asLangText("EDITOR-PREVIOUS-PAGE"))
                .hoverText(player.asLangText("EDITOR-PREVIOUS-PAGE-HOVER"))
                .runCommand("/qen editor list "+(page-1))
        }
        if (b < list.size) {
            json
                .newLine()
                .append(player.asLangText("EDITOR-NEXT-PAGE"))
                .hoverText(player.asLangText("EDITOR-NEXT-PAGE-HOVER"))
                .runCommand("/qen editor list "+(page+1))
        }
        json.newLine().sendTo(adaptPlayer(player))
    }
}