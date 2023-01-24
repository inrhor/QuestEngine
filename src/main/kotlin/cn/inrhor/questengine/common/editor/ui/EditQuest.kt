package cn.inrhor.questengine.common.editor.ui

import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.common.editor.ui.EditHome.addButton
import cn.inrhor.questengine.common.editor.ui.quest.EditQuestAccept
import cn.inrhor.questengine.common.editor.ui.quest.EditQuestMode
import cn.inrhor.questengine.common.editor.ui.quest.EditQuestTime
import cn.inrhor.questengine.common.quest.manager.QuestManager.existQuestFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager.register
import cn.inrhor.questengine.common.quest.manager.QuestManager.saveFile
import cn.inrhor.questengine.utlis.Input.inputBook
import cn.inrhor.questengine.utlis.lang
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.*

object EditQuest {

    fun addQuest(player: Player) {
        player.closeInventory()
        player.inputBook(player.asLangText("EDIT_BOOK_QUEST_ID"), true,
            player.asLangTextList("EDIT_INPUT_ADD_QUEST_ID")) {
            if (it.size>=2) {
                val id = it[1]
                if (!id.existQuestFrame()) {
                    val questFrame = QuestFrame(id)
                    editPath(player, questFrame)
                }
            }
        }
    }

    private fun editPath(player: Player, questFrame: QuestFrame) {
        submit(delay =2) {
            player.inputBook(player.asLangText("EDIT_BOOK_QUEST_PATH"), true,
                player.asLangTextList("EDIT_INPUT_ADD_QUEST_PATH")) {
                if (it.size >= 2) {
                    questFrame.path = "plugins\\QuestEngine\\space\\quest\\${it[1]}"
                    editName(player, questFrame)
                }
            }
        }
    }

    private fun editName(player: Player, questFrame: QuestFrame) {
        submit(delay =2) {
            player.inputBook(player.asLangText("EDIT_BOOK_QUEST_NAME"), true,
                player.asLangTextList("EDIT_INPUT_ADD_QUEST_NAME")) {
                if (it.size >= 2) {
                    questFrame.name = it[1]
                    questFrame.saveFile(true)
                    questFrame.register()
                    openEdit(player, questFrame)
                }
            }
        }
    }

    private fun editRename(player: Player, questFrame: QuestFrame) {
        player.closeInventory()
        player.inputBook(player.asLangText("EDIT_BOOK_QUEST_RENAME"), true,
            player.asLangTextList("EDIT_INPUT_QUEST_RENAME", questFrame.name)) {
            if (it.size >= 2) {
                questFrame.name = it[1]
                questFrame.saveFile(true)
                openEdit(player, questFrame)
            }
        }
    }

    fun openEdit(player: Player, questFrame: QuestFrame) {
        val id = questFrame.id
        player.openMenu<Basic>(player.asLangText("EDIT_UI_QUEST")) {
            rows(6)
            map("--------B", "--N#RC@", "--*P")
            addButton(player, 'B', XMaterial.ARROW, "EDIT_BACK_QUEST_LIST", id) {
                EditQuestList.open(player)
            }
            addButton(player, 'N', XMaterial.NAME_TAG, player.asLangTextList("EDIT_QUEST_RENAME", questFrame.name), id) {
                editRename(player, questFrame)
            }
            addButton(player, '#', XMaterial.WRITABLE_BOOK, "EDIT_QUEST_NOTE", id) {
                openNote(player, questFrame)
            }
            addButton(player, 'R', XMaterial.REDSTONE,
                player.asLangTextList("EDIT_QUEST_ACCEPT",
                    questFrame.accept.autoLang(player)), id) {
                EditQuestAccept.open(player, questFrame)
            }
            addButton(player, 'C', XMaterial.CLOCK,
                player.asLangTextList("EDIT_QUEST_TIME",
                    questFrame.time.langTime(player)), id) {
                EditQuestTime.open(player, questFrame)
            }
            val mode = questFrame.mode
            addButton(player, '@', XMaterial.BOW,
                player.asLangTextList("EDIT_QUEST_MODE",
                    mode.type.lang(player),
                    mode.amount,
                    mode.shareData.lang(player)), id) {
                EditQuestMode.open(player, questFrame)
            }
            addButton(player, '*', XMaterial.PAINTING, "EDIT_QUEST_CONTROL", id) {
                EditControlList.quest(player, questFrame)
            }
            addButton(player, 'P', XMaterial.PAPER, "EDIT_QUEST_TARGET", id) {
                EditTargetList.open(player, questFrame)
            }
        }
    }

    private fun editNote(player: Player, questFrame: QuestFrame) {
        player.closeInventory()
        player.inputBook(player.asLangText("EDIT_BOOK_QUEST_NOTE"), true, questFrame.note) {
            questFrame.note = it
            questFrame.saveFile(true)
            openNote(player, questFrame)
        }
    }

    fun openNote(player: Player, questFrame: QuestFrame) {
        val id = questFrame.id
        player.openMenu<Basic>(player.asLangText("EDIT_UI_QUEST_NOTE")) {
            rows(6)
            map("--------B", "--RW")
            addButton(player, 'B', XMaterial.ARROW, "EDIT_BACK_QUEST_EDIT", id) {
                openEdit(player, questFrame)
            }
            addButton(player, 'R', XMaterial.BOOK, "EDIT_QUEST_NOTE_PREVIEW", id, addList = questFrame.note)
            addButton(player, 'W', XMaterial.WRITABLE_BOOK, "EDIT_QUEST_NOTE_EDIT", id, addList = questFrame.note) {
                editNote(player, questFrame)
            }
        }
    }

}