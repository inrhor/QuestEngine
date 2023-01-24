package cn.inrhor.questengine.common.editor.ui

import cn.inrhor.questengine.api.quest.*
import cn.inrhor.questengine.common.editor.ui.EditHome.addButton
import cn.inrhor.questengine.common.editor.ui.quest.EditQuestAccept
import cn.inrhor.questengine.common.quest.manager.QuestManager.saveFile
import cn.inrhor.questengine.script.kether.errorEval
import cn.inrhor.questengine.utlis.Input.inputBook
import cn.inrhor.questengine.utlis.lineSplit
import cn.inrhor.questengine.utlis.newLineList
import org.bukkit.entity.Player
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.asLangText
import taboolib.platform.util.asLangTextList

object EditControl {

    fun open(player: Player, back: List<String>, controlFrame: ControlFrame,
             questFrame: QuestFrame, targetFrame: TargetFrame? = null,
            scriptLang: String = "EDIT_CONTROL_SCRIPT", langAddList: List<String> = listOf()) {
        val id = questFrame.id
        val t = targetFrame?.id?: id
        fun backOpen() {
            if (targetFrame == null) {
                EditControlList.quest(player, questFrame)
            }else {
                EditControlList.target(player, questFrame, targetFrame)
            }
        }
        val script = controlFrame.script
        fun editScript() {
            player.closeInventory()
            player.inputBook(player.asLangText("EDIT_BOOK_QUEST_ACCEPT_CONDITION"), true,
                script.newLineList()) {
                controlFrame.script = it.joinToString("\n")
                questFrame.saveFile()
                open(player, back, controlFrame, questFrame, targetFrame)
            }
        }
        player.openMenu<Basic>(player.asLangText("EDIT_UI_CONTROL")) {
            rows(6)
            map("--------B", "--ST#D")
            addButton(player, 'B', XMaterial.ARROW, back, t) {
                backOpen()
            }
            addButton(player, 'S', XMaterial.PLAYER_HEAD,
                player.asLangTextList("EDIT_CONTROL_SELECT", controlFrame.select.lang(player)), t) {
                selectObject(player, questFrame, controlFrame, targetFrame)
            }
            addButton(player, 'T', XMaterial.WATER_BUCKET,
                player.asLangTextList("EDIT_CONTROL_TYPE", controlFrame.type.lang(player)), t) {
                selectType(player, questFrame, controlFrame, OpenType.CHANGE, targetFrame)
            }
            val s = controlFrame.script.lineSplit().joinToString("\n").newLineList("&f")
            addButton(player, '#', XMaterial.WRITABLE_BOOK,scriptLang, t,
                addList = langAddList.ifEmpty { s }) {
                if (clickEvent().isLeftClick) {
                    editScript()
                }else if (clickEvent().isRightClick) {
                    val b = errorEval(player, script) {
                        it.rootFrame().variables().set("@QenQuestID", id)
                        it.rootFrame().variables().set("@QenTargetID", t)
                    }
                    if (b.isNotEmpty()) {
                        val addErrorList = EditQuestAccept.errorAddList(player, b, s)
                        open(player, back, controlFrame, questFrame, targetFrame,
                            "EDIT_CONTROL_SCRIPT_ERROR", addErrorList)
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

    private fun selectObject(player: Player, questFrame: QuestFrame,
                             controlFrame: ControlFrame, targetFrame: TargetFrame? = null) {
        val id = questFrame.id
        val t = targetFrame?.id?: id
        val who = if (targetFrame == null) "QUEST" else "TARGET"
        fun updateSelect() {
            questFrame.saveFile()
            open(player, player.asLangTextList("EDIT_BACK_${who}_CONTROL_LIST", t), controlFrame, questFrame, targetFrame)
        }
        player.openMenu<Basic>(player.asLangText("EDIT_UI_CONTROL_SELECT")) {
            rows(6)
            map("--------B", "--STA")
            addButton(player, 'B', XMaterial.ARROW,
                player.asLangTextList("EDIT_BACK_${who}_CONTROL_EDIT", t)) {
                open(player, player.asLangTextList("EDIT_BACK_${who}_CONTROL_LIST", t), controlFrame, questFrame)
            }
            addButton(player, 'S', XMaterial.LIME_WOOL, "EDIT_CONTROL_SELECT_SELF", t) {
                controlFrame.select = SelectObject.SELF
                updateSelect()
            }
            addButton(player, 'T', XMaterial.LIGHT_BLUE_WOOL, "EDIT_CONTROL_SELECT_TEAM", t) {
                controlFrame.select = SelectObject.TEAM
                updateSelect()
            }
            addButton(player, 'A', XMaterial.ORANGE_WOOL, "EDIT_CONTROL_SELECT_ALL", t) {
                controlFrame.select = SelectObject.ALL
                updateSelect()
            }
        }
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