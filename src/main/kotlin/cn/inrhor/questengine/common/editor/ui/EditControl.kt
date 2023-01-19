package cn.inrhor.questengine.common.editor.ui

import cn.inrhor.questengine.api.quest.ControlFrame
import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.api.quest.QueueType
import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.common.editor.ui.EditHome.addButton
import cn.inrhor.questengine.common.editor.ui.quest.EditQuestTime
import cn.inrhor.questengine.common.quest.manager.QuestManager.saveFile
import cn.inrhor.questengine.script.kether.runEvalSet
import cn.inrhor.questengine.utlis.Input.inputBook
import cn.inrhor.questengine.utlis.newLineList
import org.bukkit.entity.Player
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.asLangText
import taboolib.platform.util.asLangTextList

object EditControl {

    fun open(player: Player, back: List<String>, controlFrame: ControlFrame, questFrame: QuestFrame, targetFrame: TargetFrame? = null) {
        val id = questFrame.id
        fun backOpen() {
            if (targetFrame == null) {
                EditControlList.quest(player, questFrame)
            }else {
                EditControlList.target(player, questFrame, targetFrame)
            }
        }
        player.openMenu<Basic>(player.asLangText("EDIT_UI_CONTROL")) {
            rows(6)
            map("--------B", "--ST#D")
            addButton(player, 'B', XMaterial.ARROW, back, id) {
                backOpen()
            }
            addButton(player, 'S', XMaterial.PLAYER_HEAD,
                player.asLangTextList("EDIT_CONTROL_SELECT", controlFrame.select.lang(player)), id) {
            }
            addButton(player, 'T', XMaterial.WATER_BUCKET,
                player.asLangTextList("EDIT_CONTROL_TYPE", controlFrame.type.lang(player)), id) {
                selectType(player, questFrame, controlFrame, OpenType.CHANGE, targetFrame)
            }
            addButton(player, '#', XMaterial.WRITABLE_BOOK,"EDIT_CONTROL_SCRIPT", id,
                addList = controlFrame.script.newLineList("&f")) {
                if (clickEvent().isLeftClick) {

                }else if (clickEvent().isRightClick) {
                    runEvalSet(setOf(player), controlFrame.script) {
                        it.rootFrame().variables().set("@QenQuestID", id)
                        it.rootFrame().variables().set("@QenTargetID", targetFrame?.id?: "null")
                    }
                }
            }
            addButton(player, 'D', XMaterial.SHEARS,
                player.asLangTextList("EDIT_CONTROL_DELETE", controlFrame.type.lang(player)), id) {
                questFrame.control.remove(controlFrame)
                questFrame.saveFile()
                backOpen()
            }
        }
    }

    private enum class OpenType {
        ADD, CHANGE
    }

    private fun selectType(player: Player, questFrame: QuestFrame, controlFrame: ControlFrame,
                           openType: OpenType = OpenType.CHANGE, targetFrame: TargetFrame? = null) {
        val id = questFrame.id
        val t = targetFrame?.id?: id
        val who = if (targetFrame == null) "QUEST" else "TARGET"
        fun controlAdd() {
            if (openType == OpenType.ADD) {
                if (targetFrame == null) {
                    questFrame.control.add(controlFrame)
                }else {
                    targetFrame.trigger.add(controlFrame)
                }
            }
            questFrame.saveFile()
            open(player, player.asLangTextList("EDIT_BACK_${who}_CONTROL_LIST", t), controlFrame, questFrame, targetFrame)
        }
        player.openMenu<Basic>(player.asLangText("EDIT_UI_CONTROL_TYPE")) {
            rows(6)
            map("--------B", "--AIFQR")
            if (openType == OpenType.ADD) {
                addButton(player, 'B', XMaterial.ARROW,
                    player.asLangTextList("EDIT_BACK_${who}_CONTROL_LIST", t)) {
                    if (targetFrame == null) {
                        EditControlList.quest(player, questFrame)
                    }else EditControlList.target(player, questFrame, targetFrame)
                }
            }else {
                addButton(player, 'B', XMaterial.ARROW,
                    player.asLangTextList("EDIT_BACK_${who}_CONTROL_EDIT", t)) {
                    open(player, player.asLangTextList("EDIT_BACK_${who}_CONTROL_LIST", t), controlFrame, questFrame)
                }
            }
            addButton(player, 'A', XMaterial.LIME_WOOL, "EDIT_CONTROL_TYPE_ACCEPT", t) {
                controlFrame.type = QueueType.ACCEPT
                controlAdd()
            }
            addButton(player, 'I', XMaterial.LIGHT_BLUE_WOOL, "EDIT_CONTROL_TYPE_FINISH", t) {
                controlFrame.type = QueueType.FINISH
                controlAdd()
            }
            addButton(player, 'F', XMaterial.RED_WOOL, "EDIT_CONTROL_TYPE_FAIL", t) {
                controlFrame.type = QueueType.FAIL
                controlAdd()
            }
            addButton(player, 'Q', XMaterial.WHITE_WOOL, "EDIT_CONTROL_TYPE_QUIT", t) {
                controlFrame.type = QueueType.QUIT
                controlAdd()
            }
            addButton(player, 'R', XMaterial.ORANGE_WOOL, "EDIT_CONTROL_TYPE_RESET", t) {
                controlFrame.type = QueueType.RESET
                controlAdd()
            }
        }
    }

    fun openAdd(player: Player, questFrame: QuestFrame, targetFrame: TargetFrame? = null) {
        player.closeInventory()
        player.inputBook(player.asLangText("EDIT_BOOK_QUEST_CONTROL"), true,
            player.asLangTextList("EDIT_INPUT_ADD_CONTROL")) {
                val control = ControlFrame(it[1])
                selectType(player, questFrame, control, OpenType.ADD, targetFrame)
            }
    }

}