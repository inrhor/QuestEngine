package cn.inrhor.questengine.common.editor.ui

import cn.inrhor.questengine.api.manager.InputManager.inputChat
import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.common.editor.ui.EditHome.addButton
import cn.inrhor.questengine.utlis.lang
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.asLangText
import taboolib.platform.util.asLangTextList

object EditQuest {

    fun addQuest(player: Player) {
        player.inputChat("EDIT-INPUT-QUEST-ID", "quest")
    }

    /**
     * 先addQuest
     * 聊天框输入后打开编辑任务
     */
    fun openEdit(player: Player, questFrame: QuestFrame) {
        val id = questFrame.id
        player.openMenu<Basic>(player.asLangText("EDIT_UI_QUEST")) {
            rows(6)
            map("--------B", "--N#RC@", "--*P")
            addButton(player, 'B', Material.BARRIER, "EDIT_BACK_QUEST_LIST", id) {
                EditQuestList.open(player)
            }
            addButton(player, 'N', Material.NAME_TAG, player.asLangTextList("EDIT_QUEST_RENAME", questFrame.name), id) {
                openNote(player, questFrame)
            }
            addButton(player, '#', Material.WRITABLE_BOOK, "EDIT_QUEST_NOTE", id) {
                openNote(player, questFrame)
            }
            addButton(player, 'R', Material.REDSTONE,
                player.asLangTextList("EDIT_QUEST_ACCEPT",
                    questFrame.accept.autoLang(player)), id) {

            }
            addButton(player, 'C', Material.CLOCK,
                player.asLangTextList("EDIT_QUEST_TIME",
                    questFrame.time.langTime(player)), id) {

            }
            val mode = questFrame.mode
            addButton(player, '@', Material.BOW,
                player.asLangTextList("EDIT_QUEST_MODE",
                    mode.type.lang(player),
                    mode.amount,
                    mode.shareData.lang(player)), id) {

            }
            addButton(player, '*', Material.PAINTING, "EDIT_QUEST_CONTROL", id) {
                EditControlList.quest(player, questFrame)
            }
            addButton(player, 'P', Material.PAPER, "EDIT_QUEST_TARGET", id) {
                EditTargetList.open(player, questFrame)
            }
        }
    }

    fun openNote(player: Player, questFrame: QuestFrame) {
        val id = questFrame.id
        player.openMenu<Basic>(player.asLangText("EDIT_UI_QUEST_NOTE")) {
            rows(6)
            map("--------B", "--RW")
            addButton(player, 'B', Material.BARRIER, "EDIT_BACK_QUEST_EDIT", id) {
                openEdit(player, questFrame)
            }
            addButton(player, 'R', Material.BOOK, "EDIT_QUEST_NOTE_PREVIEW", id, questFrame.note) {

            }
            addButton(player, 'W', Material.WRITABLE_BOOK, "EDIT_QUEST_NOTE_EDIT", id, questFrame.note) {

            }
        }
    }

}