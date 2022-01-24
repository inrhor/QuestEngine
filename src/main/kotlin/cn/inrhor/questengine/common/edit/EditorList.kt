package cn.inrhor.questengine.common.edit

import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.platform.util.asLangText

object EditorList {
    /**
     * 可视化 - 任务列表
     */
    fun Player.editorListQuest(page: Int = 0) {
        val list = QuestManager.questMap.values.toMutableList()
        EditorQuestList(this, asLangText("EDITOR-LIST-QUEST"))
            .list(page, 7, list, true, "EDITOR-LIST-QUEST-INFO", "qen editor quest list",
                EditorListModule.EditorButton("EDITOR-LIST-QUEST-EDIT"),
                EditorListModule.EditorButton("EDITOR-LIST-QUEST-EDIT-META",
                    "EDITOR-LIST-QUEST-EDIT-HOVER", "/qen editor quest edit"),
                EditorListModule.EditorButton("EDITOR-LIST-QUEST-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-QUEST-DEL-META",
                    "EDITOR-LIST-QUEST-DEL-HOVER", "/qen editor quest del"))
            .json.sendTo(adaptPlayer(this))
        /*val json = TellrawJson().newLine().append("   "+asLangText("EDITOR-LIST-QUEST")).newLine()
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
                .append("      "+asLangText("EDITOR-LIST-QUEST-INFO", it.questID, it.name))
                .append("   "+asLangText("EDITOR-LIST-QUEST-EDIT"))
                .append(" "+asLangText("EDITOR-LIST-QUEST-EDIT-META"))
                .hoverText(asLangText("EDITOR-LIST-QUEST-EDIT-HOVER", it.questID))
                .runCommand("/qen editor quest edit "+it.questID)
                .append("  "+asLangText("EDITOR-LIST-QUEST-DEL"))
                .append(" "+asLangText("EDITOR-LIST-QUEST-DEL-META"))
                .hoverText(asLangText("EDITOR-LIST-QUEST-DEL-HOVER", it.questID))
                .runCommand("/qen editor quest del "+it.questID)
                .newLine()
        }
        if (page > 0) {
            json
                .newLine()
                .append("   "+asLangText("EDITOR-PREVIOUS-PAGE"))
                .hoverText(asLangText("EDITOR-PREVIOUS-PAGE-HOVER"))
                .runCommand("/qen editor quest list "+(page-1))
        }
        if ((page+1)*7 <= list.size-1) {
            json
                .newLine()
                .append("   "+asLangText("EDITOR-NEXT-PAGE"))
                .hoverText(asLangText("EDITOR-NEXT-PAGE-HOVER"))
                .runCommand("/qen editor quest list "+(page+1))
        }
        json.newLine().sendTo(adaptPlayer(this))*/
    }

    fun Player.editorListInner(questID: String, page: Int = 0) {
        val questModule = QuestManager.getQuestModule(questID)?: return
        EditorInnerList(this, asLangText("EDITOR-EDIT-QUEST-INNER-START", questID))
            .list(page, 7, questModule.innerQuestList, true, "EDITOR-EDIT-QUEST-INNER-LIST",
                "qen editor quest edit innerlist").json.sendTo(adaptPlayer(this))
    }
}