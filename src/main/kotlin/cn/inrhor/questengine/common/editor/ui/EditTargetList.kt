package cn.inrhor.questengine.common.editor.ui

import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.common.editor.ui.EditHome.addButton
import cn.inrhor.questengine.script.kether.evalStringList
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.asLangText
import taboolib.platform.util.asLangTextList
import taboolib.platform.util.buildItem

object EditTargetList {

    fun open(player: Player, questFrame: QuestFrame) {
        val id = questFrame.id
        player.openMenu<Linked<TargetFrame>>(player.asLangText("EDIT_UI_TARGET_LIST")) {
            rows(6)
            slots(listOf(10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34))
            addButton(player, 8, Material.BARRIER, "EDIT_BACK_QUEST_EDIT", id) {
               EditQuest.openEdit(player, questFrame)
            }
            elements { questFrame.target }
            onGenerate { _, element, _, _ ->
                buildItem(Material.MAP) {
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
        }
    }

}