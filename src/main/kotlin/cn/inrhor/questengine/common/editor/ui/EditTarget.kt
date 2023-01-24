package cn.inrhor.questengine.common.editor.ui

import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.common.editor.ui.EditHome.addButton
import cn.inrhor.questengine.common.editor.ui.target.EditTargetEvent
import cn.inrhor.questengine.common.quest.manager.QuestManager.saveFile
import cn.inrhor.questengine.utlis.Input.inputBook
import org.bukkit.entity.Player
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.asLangText
import taboolib.platform.util.asLangTextList

object EditTarget {

    fun open(player: Player, questFrame: QuestFrame, targetFrame: TargetFrame) {
        val id = questFrame.id
        player.openMenu<Basic>(player.asLangText("EDIT_UI_TARGET")) {
            rows(6)
            map("--------B", "--#WC")
            addButton(player, 'B', XMaterial.ARROW, "EDIT_BACK_TARGET_LIST", id) {
                EditTargetList.open(player, questFrame)
            }
            addButton(player, '#', XMaterial.IRON_AXE,
                player.asLangTextList("EDIT_TARGET_EVENT", targetFrame.event), id) {
                EditTargetEvent.open(player, questFrame, targetFrame)
            }
            addButton(player, 'W', XMaterial.WRITABLE_BOOK,
                "EDIT_TARGET_NOTE", id, addList = targetFrame.description) {
                player.closeInventory()
                player.inputBook(player.asLangText("EDIT_BOOK_TARGET_NOTE"), true, targetFrame.description) {
                    targetFrame.description = it
                    questFrame.saveFile(true)
                    open(player, questFrame, targetFrame)
                }
            }
            addButton(player, 'C', XMaterial.PAINTING,
                player.asLangTextList("EDIT_TARGET_TRIGGER"), id) {
                EditControlList.target(player, questFrame, targetFrame)
            }
        }
    }

}