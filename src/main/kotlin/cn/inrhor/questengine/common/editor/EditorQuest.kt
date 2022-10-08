package cn.inrhor.questengine.common.editor

import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestFrame
import cn.inrhor.questengine.utlis.lang
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

object EditorQuest {

    val editQuestMeta = listOf(
        "NAME", "NOTE", "GROUPEXTENDS", "GROUPNUMBER", "GROUPNOTE",
        "MODETYPE", "MODEAMOUNT", "SHAREDATA",
        "TIME", "ACCEPTAUTO", "ACCEPTCONDITION", "CONTROL", "TARGET")

    fun Player.editorQuest(questID: String) {
        val quest = questID.getQuestFrame()
        val json = TellrawJson()
            .newLine()
            .append("   "+asLangText("EDITOR-EDIT-QUEST", questID))
            .newLine()
            .append("      "+asLangText("EDITOR-BACK"))
            .append("  "+asLangText("EDITOR-BACK-META"))
            .hoverText(asLangText("EDITOR-BACK-HOVER"))
            .runCommand("/qen eval editor quest in list page 0")
            .newLine()
            .newLine()
        editQuestMeta.forEach {
            val group = quest.group
            val mode = quest.mode
            val accept = quest.accept
            json.append("      "+asLangText("EDITOR-EDIT-QUEST-$it",
                quest.name, group.extends, group.number,
                mode.type.lang(this), mode.amount,
                mode.shareData.lang(this),
                accept.autoLang(this)))
                .append("  "+asLangText("EDITOR-EDIT-QUEST-META"))
                .hoverText(asLangText("EDITOR-EDIT-QUEST-META-HOVER"))
            if (listOf("NOTE", "GROUPNOTE", "ACCEPTCONDITION").contains(it)) {
                json.runCommand("/qen eval quest select $questID editor quest in edit "+it.lowercase()+" page 0")
            }else {
                json.runCommand("/qen eval quest select $questID editor quest in edit "+it.lowercase())
            }
            json.newLine()
        }
        json.newLine().sendTo(adaptPlayer(this))
    }

}