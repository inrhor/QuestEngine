package cn.inrhor.questengine.common.editor.ui.quest

import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.common.editor.ui.EditHome.addButton
import cn.inrhor.questengine.common.editor.ui.EditQuest
import cn.inrhor.questengine.common.quest.enum.ModeType
import cn.inrhor.questengine.common.quest.manager.QuestManager.saveFile
import cn.inrhor.questengine.utlis.Input.inputBook
import cn.inrhor.questengine.utlis.lang
import org.bukkit.entity.Player
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.asLangText
import taboolib.platform.util.asLangTextList

object EditQuestMode {

    fun open(player: Player, questFrame: QuestFrame) {
        val id = questFrame.id
        val mode = questFrame.mode
        val type = mode.type
        player.openMenu<Basic>(player.asLangText("EDIT_UI_QUEST_MODE")) {
            rows(6)
            map("--------B", "--TDR")
            addButton(player, 'B', XMaterial.ARROW, "EDIT_BACK_QUEST_EDIT", id) {
                EditQuest.openEdit(player, questFrame)
            }
            addButton(player, 'T', XMaterial.STONE_SWORD,
                player.asLangTextList("EDIT_QUEST_MODE_TYPE", type.lang(player)), id) {
                questFrame.mode.type = if (type == ModeType.PERSONAL) ModeType.COLLABORATION else ModeType.PERSONAL
                questFrame.saveFile()
                open(player, questFrame)
            }
            addButton(player, 'D', XMaterial.PLAYER_HEAD,
                player.asLangTextList("EDIT_QUEST_MODE_AMOUNT", mode.amount), id) {
                if (type == ModeType.COLLABORATION) {
                    player.closeInventory()
                    player.inputBook(player.asLangText("EDIT_BOOK_QUEST_MODE"), true,
                        listOf(mode.amount.toString())) {
                        questFrame.mode.amount = it.joinToString().toInt()
                        questFrame.saveFile()
                        open(player, questFrame)
                    }
                }
            }
            addButton(player, 'R', XMaterial.CHEST,
                player.asLangTextList("EDIT_QUEST_MODE_SHARE",
                    mode.shareData.lang(player)), id) {
                if (type == ModeType.COLLABORATION) {
                    questFrame.mode.shareData = !mode.shareData
                    questFrame.saveFile()
                    open(player, questFrame)
                }
            }
        }
    }

}