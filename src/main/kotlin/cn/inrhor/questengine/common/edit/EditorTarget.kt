package cn.inrhor.questengine.common.edit

import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

object EditorTarget {

   /* fun Player.editorTarget(questID: String, innerID: String, id: String) {
        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return
        val target = inner.target
        val json = TellrawJson()
            .newLine()
            .append("   "+asLangText("EDITOR-EDIT-TARGET", questID, innerID, name))
            .newLine().newLine()
        target.condition.forEach { (t, u) ->
            json.append("      "+asLangText("EDITOR-EDIT-TARGET-VALUE", t, u))
                .newLine()
        }
        target.conditionList.forEach { (t, u) ->
            json.append("      "+asLangText("EDITOR-EDIT-TARGET-KEY", t)).newLine()
            u.forEach {
                json.append("        "+asLangText("EDITOR-EDIT-TARGET-VALUE-LIST", it)).newLine()
            }
        }
        json.newLine().sendTo(adaptPlayer(this))
    }*/

}