package cn.inrhor.questengine.common.edit

import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.utlis.lang
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

object EditorQuest {

    val editQuestMeta = listOf(
        "NAME", "INNER", "START", "SORT", "MODETYPE", "MODEAMOUNT", "SHAREDATA",
        "ACCEPTWAY", "MAXQUANTITY", "ACCEPTCONDITION", "FAILURECONDITION", "FAILURESCRIPT")

    fun Player.editorQuest(questID: String) {
        val questModule = QuestManager.getQuestModule(questID)?: return
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
            json.append("      "+asLangText("EDITOR-EDIT-QUEST-$it",
                questModule.name, questModule.startInnerQuestID, questModule.sort,
                questModule.mode.modeTypeLang(this), questModule.mode.amount,
                questModule.mode.shareData.lang(this),
                questModule.accept.wayLang(this),
                questModule.accept.maxQuantity))
                .append("  "+asLangText("EDITOR-EDIT-QUEST-META"))
                .hoverText(asLangText("EDITOR-EDIT-QUEST-META-HOVER"))
            if (it == "INNER") {
                json.runCommand("/qen eval editor inner in list page 0 select $questID")
            }else if (listOf("ACCEPTCONDITION", "FAILURECONDITION", "FAILURESCRIPT").contains(it)) {
                json.runCommand("/qen eval editor quest in edit "+it.lowercase()+" page 0 select $questID")
            }else {
                json.runCommand("/qen eval editor quest in edit "+it.lowercase()+" select $questID")
            }
            json.newLine()
        }
        json.newLine().sendTo(adaptPlayer(this))
    }

}