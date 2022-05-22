package cn.inrhor.questengine.common.editor

import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

object EditorTarget {

    val editMeta = listOf(
        "NAME", "REWARD", "ASYNC", "PERIOD", "CONDITION", "NODE")

   fun Player.editorTarget(questID: String, innerID: String, targetID: String) {
        val target = QuestManager.getTargetModule(questID, innerID, targetID)?: return
        val json = TellrawJson()
            .newLine()
            .append("   "+asLangText("EDITOR-EDIT-TARGET", questID, innerID, targetID))
            .newLine()
            .append("      "+asLangText("EDITOR-BACK"))
            .append("  "+asLangText("EDITOR-BACK-META"))
            .hoverText(asLangText("EDITOR-BACK-HOVER"))
            .runCommand("/qen eval editor target in list page 0 select $questID $innerID $targetID")
            .newLine()
            .newLine()
       editMeta.forEach {
           json.append("      "+asLangText("EDITOR-EDIT-TARGET-$it",
               target.name, target.reward, target.period, target.async))
               .append("  "+asLangText("EDITOR-EDIT-TARGET-META"))
               .hoverText(asLangText("EDITOR-EDIT-TARGET-META-HOVER"))
           if (it == "CONDITION") {
               json.runCommand("/qen eval editor target in edit "+it.lowercase()+" page 0 select $questID $innerID $targetID")
           }else {
               json.runCommand("/qen eval editor target in edit "+it.lowercase()+" select $questID $innerID $targetID")
           }
           json.newLine()
       }
        json.newLine().sendTo(adaptPlayer(this))
    }

}