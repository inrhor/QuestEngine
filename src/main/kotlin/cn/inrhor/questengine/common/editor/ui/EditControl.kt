package cn.inrhor.questengine.common.editor.ui

import cn.inrhor.questengine.api.quest.ControlFrame
import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.common.editor.ui.EditHome.addButton
import cn.inrhor.questengine.script.kether.runEvalSet
import cn.inrhor.questengine.utlis.newLineList
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.asLangText
import taboolib.platform.util.asLangTextList

object EditControl {

    fun open(player: Player, back: List<String>, controlFrame: ControlFrame, questFrame: QuestFrame, targetFrame: TargetFrame? = null) {
        val id = questFrame.id
        player.openMenu<Basic>(player.asLangText("EDIT_UI_QUEST_CONTROL")) {
            rows(6)
            map("--------B", "--ST#")
            addButton(player, 'B', Material.BARRIER, back, id) {
                if (targetFrame == null) {
                    EditControlList.quest(player, questFrame)
                }else {
                    EditControlList.target(player, questFrame, targetFrame)
                }
            }
            addButton(player, 'S', Material.PLAYER_HEAD,
                player.asLangTextList("EDIT_CONTROL_SELECT", controlFrame.select.lang(player)), id) {

            }
            addButton(player, 'T', Material.WATER_BUCKET,
                player.asLangTextList("EDIT_CONTROL_TYPE", controlFrame.type.lang(player)), id) {

            }
            addButton(player, '#', Material.WRITABLE_BOOK,"EDIT_CONTROL_SCRIPT", id,
                controlFrame.script.newLineList("&f")) {
                if (clickEvent().isLeftClick) {

                }else if (clickEvent().isRightClick) {
                    runEvalSet(setOf(player), controlFrame.script) {
                        it.rootFrame().variables().set("@QenQuestID", id)
                        it.rootFrame().variables().set("@QenTargetID", targetFrame?.id?: "null")
                    }
                }
            }
        }
    }

}