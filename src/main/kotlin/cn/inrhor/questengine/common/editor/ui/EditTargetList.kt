package cn.inrhor.questengine.common.editor.ui

import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.common.editor.ui.EditHome.addButton
import cn.inrhor.questengine.common.editor.ui.EditHome.pageItem
import cn.inrhor.questengine.common.quest.manager.QuestManager.getTargetFrame
import cn.inrhor.questengine.script.kether.evalStringList
import cn.inrhor.questengine.utlis.Input.inputBook
import org.bukkit.entity.Player
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.asLangText
import taboolib.platform.util.asLangTextList
import taboolib.platform.util.buildItem
import taboolib.platform.util.sendLang

object EditTargetList {

    fun open(player: Player, questFrame: QuestFrame) {
        val id = questFrame.id
        player.openMenu<Linked<TargetFrame>>(player.asLangText("EDIT_UI_TARGET_LIST")) {
            rows(6)
            slots(listOf(10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34))
            addButton(player, 8, XMaterial.ARROW, "EDIT_BACK_QUEST_EDIT", id) {
               EditQuest.openEdit(player, questFrame)
            }
            elements { questFrame.target }
            onGenerate { _, element, _, _ ->
                buildItem(XMaterial.MAP) {
                    name = "§f                                        "
                    lore.addAll(player.evalStringList(player.asLangTextList("EDIT_TARGET_LIST", element.id)) {
                        it.rootFrame().variables().set("@QenQuestID", id)
                        it.rootFrame().variables().set("@QenTargetID", element.id)
                    })
                }
            }
            onClick { _, element ->
                EditTarget.open(player, questFrame, element)
            }
            addButton(player, 40, XMaterial.FEATHER, "EDIT_TARGET_ADD", id) {
                openAdd(player, questFrame)
            }
            pageItem(player)
        }
    }

    private fun openAdd(player: Player, questFrame: QuestFrame) {
        player.closeInventory()
        player.inputBook(player.asLangText("EDIT_BOOK_QUEST_TARGET"), true,
            player.asLangTextList("EDIT_INPUT_ADD_TARGET")) {
            val id = it[1]
            if (questFrame.getTargetFrame(id) != null) {
                player.sendLang("EXIT_TARGET_ID", questFrame.id)
                open(player, questFrame)
            }else {
                val target = TargetFrame(id)
                questFrame.target.add(target)
                EditTarget.open(player, questFrame, target)
            }
        }
    }

}