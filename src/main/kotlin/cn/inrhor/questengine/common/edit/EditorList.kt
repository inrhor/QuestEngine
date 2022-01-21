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
    fun Player.editorListQuest(page: Int = 0) {
        val json = TellrawJson().newLine().append("   "+this.asLangText("EDITOR-LIST-QUEST")).newLine()
        val list = QuestManager.questMap.values.toMutableList()
        val a = page*7
        val b = if (a < list.size) a else list.size-1
        for ((limit, i) in (b until list.size).withIndex()) {
            if (limit >= 7) {
                break
            }
            val it = list[i]
            json
                .newLine()
                .append("      "+this.asLangText("EDITOR-LIST-QUEST-INFO", it.questID, it.name))
                .append("   "+this.asLangText("EDITOR-LIST-QUEST-EDIT"))
                .append(" "+this.asLangText("EDITOR-LIST-QUEST-EDIT-META"))
                .hoverText(this.asLangText("EDITOR-LIST-QUEST-EDIT-HOVER", it.questID))
                .runCommand("/qen editor quest edit quest "+it.questID)
                .append("  "+this.asLangText("EDITOR-LIST-QUEST-DEL"))
                .append(" "+this.asLangText("EDITOR-LIST-QUEST-DEL-META"))
                .hoverText(this.asLangText("EDITOR-LIST-QUEST-DEL-HOVER", it.questID))
                .runCommand("/qen editor quest del quest "+it.questID)
                .newLine()
        }
        if (page > 0) {
            json
                .newLine()
                .append("   "+this.asLangText("EDITOR-PREVIOUS-PAGE"))
                .hoverText(this.asLangText("EDITOR-PREVIOUS-PAGE-HOVER"))
                .runCommand("/qen editor quest list "+(page-1))
        }
        if ((page+1)*7 <= list.size-1) {
            json
                .newLine()
                .append("   "+this.asLangText("EDITOR-NEXT-PAGE"))
                .hoverText(this.asLangText("EDITOR-NEXT-PAGE-HOVER"))
                .runCommand("/qen editor quest list "+(page+1))
        }
        json.newLine().sendTo(adaptPlayer(this))
    }
}