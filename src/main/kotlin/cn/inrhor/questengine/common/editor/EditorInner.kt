package cn.inrhor.questengine.common.editor

import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

object EditorInner {

    val editInnerMeta = listOf(
        "NAME", "TIME", "DESC")

    fun Player.editorInner(questID: String, innerID: String) {
        val inner = QuestManager.getInnerModule(questID, innerID)?: return
        val json = TellrawJson()
            .newLine()
            .append("   "+asLangText("EDITOR-EDIT-INNER", questID, innerID))
            .newLine()
            .append("      "+asLangText("EDITOR-BACK"))
            .append("  "+asLangText("EDITOR-BACK-META"))
            .hoverText(asLangText("EDITOR-BACK-HOVER"))
            .runCommand("/qen eval quest select $questID inner select $innerID editor inner in list page 0")
            .newLine()
            .newLine()
        editInnerMeta.forEach {
            json.append("      "+asLangText("EDITOR-EDIT-INNER-$it",
                inner.name, inner.time.langTime(this)))
                .append("  "+asLangText("EDITOR-EDIT-INNER-META"))
                .hoverText(asLangText("EDITOR-EDIT-INNER-META-HOVER"))
            if (it == "NEXTINNER") {
                json.runCommand("/qen eval quest select $questID inner select $innerID editor inner in edit "+it.lowercase()+" page 0")
            }else {
                json.runCommand("/qen eval quest select $questID inner select $innerID editor inner in edit "+it.lowercase())
            }
            json.newLine()
        }
        listOf("TARGET", "REWARD", "FAIL").forEach {
            json.append("      "+asLangText("EDITOR-EDIT-INNER-$it",
                inner.name))
                .append("  "+asLangText("EDITOR-EDIT-INNER-META"))
                .hoverText(asLangText("EDITOR-EDIT-INNER-META-HOVER"))
                .runCommand("/qen eval quest select $questID inner select $innerID editor ${it.lowercase()} in list page 0")
                .newLine()
        }
        json.newLine().sendTo(adaptPlayer(this))
    }

}