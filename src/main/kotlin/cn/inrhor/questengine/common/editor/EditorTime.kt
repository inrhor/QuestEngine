package cn.inrhor.questengine.common.editor

import cn.inrhor.questengine.api.quest.module.inner.TimeFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

object EditorTime {

    fun Player.editTime(questID: String, innerID: String) {
        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return
        val time = inner.time
        val json = TellrawJson()
            .newLine()
            .append("   "+asLangText("EDITOR-EDIT-TIME", questID, innerID))
            .newLine()
            .append("      "+asLangText("EDITOR-BACK"))
            .append("  "+asLangText("EDITOR-BACK-META"))
            .hoverText(asLangText("EDITOR-BACK-HOVER"))
            .runCommand("/qen eval editor inner in edit home select $questID $innerID")
            .newLine()
            .newLine()
            .append("      "+asLangText("EDITOR-EDIT-TIME-TYPE",
                asLangText("EDITOR-EDIT-TIME-TYPE-${time.type}")))
            .append("  "+asLangText("EDITOR-EDIT-TIME-META"))
            .hoverText(asLangText("EDITOR-EDIT-TIME-META-HOVER"))
            .runCommand("/qen eval editor time in edit type select $questID $innerID").newLine()
            .append("      "+asLangText("EDITOR-EDIT-TIME-${time.type}", time.langTime(this)))
            if (time.type != TimeFrame.Type.ALWAYS) {
                json.append("  "+asLangText("EDITOR-EDIT-TIME-META"))
                    .hoverText(asLangText("EDITOR-EDIT-TIME-META-HOVER"))
                    .runCommand("/qen eval editor time in edit ${time.type} select $questID $innerID")
            }
            json.newLine().sendTo(adaptPlayer(this))
    }

}