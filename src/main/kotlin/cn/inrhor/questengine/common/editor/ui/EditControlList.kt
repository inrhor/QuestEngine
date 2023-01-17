package cn.inrhor.questengine.common.editor.ui

import cn.inrhor.questengine.api.quest.ControlFrame
import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.common.editor.ui.EditHome.addButton
import cn.inrhor.questengine.script.kether.evalStringList
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.module.ui.ClickEvent
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.asLangText
import taboolib.platform.util.asLangTextList
import taboolib.platform.util.buildItem

object EditControlList {

    fun quest(player: Player, questFrame: QuestFrame) {
        use(player, questFrame,
            "EDIT_UI_QUEST_CONTROL",
            "EDIT_BACK_QUEST_EDIT") {
            EditQuest.openEdit(player, questFrame)
        }
    }

    fun target(player: Player, questFrame: QuestFrame, targetFrame: TargetFrame) {
        use(player, questFrame,
            "EDIT_UI_TARGET_CONTROL",
            "EDIT_BACK_TARGET_EDIT", targetFrame)
    }

    private fun use(player: Player, questFrame: QuestFrame, titleLang: String, back: String, targetFrame: TargetFrame? = null, action: ClickEvent.() -> Unit = {}) {
        val id = questFrame.id
        player.openMenu<Linked<ControlFrame>>(player.asLangText(titleLang)) {
            rows(6)
            slots(listOf(10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34))
            addButton(player, 8, Material.BARRIER, back, id) {
                action()
            }
            elements { questFrame.control }
            onGenerate { _, element, _, _ ->
                buildItem(Material.COMPARATOR) {
                    name = "§f                                        "
                    lore.addAll(player.evalStringList(player.asLangTextList("EDIT_CONTROL_LIST", element.id)) {
                        it.rootFrame().variables().set("@QenQuestID", id)
                    })
                }
            }
            onClick { _, element ->
                EditControl.open(player,
                    player.asLangTextList("EDIT_BACK_QUEST_CONTROL_LIST", id), element, questFrame)
            }
        }
    }

}