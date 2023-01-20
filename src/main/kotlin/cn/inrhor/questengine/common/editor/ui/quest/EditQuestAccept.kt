package cn.inrhor.questengine.common.editor.ui.quest

import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.common.editor.ui.EditHome.addButton
import cn.inrhor.questengine.common.editor.ui.EditQuest
import cn.inrhor.questengine.common.quest.manager.QuestManager.saveFile
import cn.inrhor.questengine.script.kether.testEval
import cn.inrhor.questengine.utlis.Input.inputBook
import cn.inrhor.questengine.utlis.lineSplit
import cn.inrhor.questengine.utlis.newLineList
import org.bukkit.entity.Player
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.asLangText
import taboolib.platform.util.asLangTextList

object EditQuestAccept {

    fun errorAddList(player: Player, errorInfo: String, list:  MutableList<String>): MutableList<String> {
        if (errorInfo.isNotEmpty()) {
            list.add("")
            list.add(player.asLangText("ERROR_INFO"))
            // 对string进行换行，每行最多50个字符
            errorInfo.lineSplit(list)
        }
        return list
    }

    fun open(player: Player, questFrame: QuestFrame) {
        val id = questFrame.id
        val accept = questFrame.accept
        val co = accept.condition
        val back = testEval(player, co)
        val backInfo = back.errorInfo(player, co) {
            it.rootFrame().variables().set("@QenQuestID", id)
        }
        val list = co.lineSplit().joinToString("\n").newLineList("&f")
        val coList = errorAddList(player, backInfo, list)
        player.openMenu<Basic>(player.asLangText("EDIT_UI_QUEST_ACCEPT")) {
            rows(6)
            map("--------B", "--E#")
            addButton(player, 'B', XMaterial.ARROW, "EDIT_BACK_QUEST_EDIT", id) {
                EditQuest.openEdit(player, questFrame)
            }
            addButton(player, 'E', XMaterial.TORCH,
                player.asLangTextList("EDIT_QUEST_ACCEPT_TOGGLE", accept.autoLang(player)), id) {
                questFrame.accept.auto = !questFrame.accept.auto
                questFrame.saveFile()
                open(player, questFrame)
            }
            addButton(player, '#', XMaterial.REDSTONE_TORCH,
                player.asLangTextList("EDIT_QUEST_ACCEPT_CONDITION",
                    back.lang(player, "CONDITION_RETURN")), id, addList = coList) {
                editCondition(player, questFrame)
            }
        }
    }

    private fun editCondition(player: Player, questFrame: QuestFrame) {
        player.closeInventory()
        player.inputBook(player.asLangText("EDIT_BOOK_QUEST_ACCEPT_CONDITION"), true,
            questFrame.accept.condition.newLineList()) {
            questFrame.accept.condition = it.joinToString("\n")
            questFrame.saveFile()
            open(player, questFrame)
        }
    }

}