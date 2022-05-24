package cn.inrhor.questengine.common.editor

import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

object EditorTarget {

    val editMeta = mutableListOf(
        "NAME", "REWARD", "REWARD_BOOLEAN", "ASYNC", "CONDITION")

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
       if (target.name == "TASK") editMeta.add("PERIOD")
       editMeta.forEach {
           val r = target.reward
           val rewardID = if (r.isEmpty()) " " else r.split(" ")[0]
           val b = if (r.isEmpty()) "NULL" else r.split(" ")[1].uppercase()
           if (it == "REWARD_BOOLEAN") {
               json.append("  "+asLangText("EDITOR-EDIT-TARGET-$it", asLangText("REWARD-BOOLEAN-META-$b")))
                   .hoverText(asLangText("EDITOR-EDIT-TARGET-BOOLEAN-META-HOVER"))
           }else {
               val t = "${target.async}".uppercase()
               json.append("      " + asLangText("EDITOR-EDIT-TARGET-$it",
                       target.name, rewardID, target.period, asLangText("ASYNC-BOOLEAN-META-$t")))
                   .append("  " + asLangText("EDITOR-EDIT-TARGET-META"))
               if (it == "ASYNC") {
                   json.hoverText(asLangText("EDITOR-EDIT-TARGET-BOOLEAN-META-HOVER"))
               }else {
                   json.hoverText(asLangText("EDITOR-EDIT-TARGET-META-HOVER"))
               }
           }
           when (it) {
               "CONDITION" -> {
                   json.runCommand("/qen eval editor target in edit "+it.lowercase()+" page 0 select $questID $innerID $targetID").newLine()
               }
               "REWARD" -> {
                   json.runCommand("/qen eval editor target in sel "+it.lowercase()+" page 0 select $questID $innerID $targetID")
               }
               else -> {
                   json.runCommand("/qen eval editor target in edit "+it.lowercase()+" select $questID $innerID $targetID").newLine()
               }
           }
       }
        json.newLine().sendTo(adaptPlayer(this))
    }

}