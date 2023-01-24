package cn.inrhor.questengine.common.editor.ui

import cn.inrhor.questengine.api.quest.GroupFrame
import cn.inrhor.questengine.common.editor.ui.EditHome.addButton
import cn.inrhor.questengine.common.editor.ui.EditHome.pageItem
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.script.kether.evalStringList
import org.bukkit.entity.Player
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.asLangText
import taboolib.platform.util.asLangTextList
import taboolib.platform.util.buildItem

object EditGroupList {

    fun open(player: Player) {
        player.openMenu<Linked<GroupFrame>>(player.asLangText("EDIT_UI_GROUP_LIST")) {
            rows(6)
            slots(listOf(10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34))
            addButton(player, 8, XMaterial.ARROW, "EDIT_BACK_QUEST_HOME") {
                EditHome.open(player)
            }
            elements { QuestManager.groupMap.values.toList() }
            onGenerate { _, element, _, _ ->
                buildItem(XMaterial.BOOK) {
                    name = "§f                                        "
                    lore.addAll(player.evalStringList(
                        player.asLangTextList("EDIT_GROUP_LIST", element.id, element.name)){})
                }
            }
            onClick { _, element ->
            }
            pageItem(player)
        }
    }

}