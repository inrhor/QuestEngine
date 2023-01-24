package cn.inrhor.questengine.common.editor.ui

import cn.inrhor.questengine.script.kether.evalStringList
import org.bukkit.entity.Player
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.ClickEvent
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked
import taboolib.platform.util.asLangText
import taboolib.platform.util.asLangTextList
import taboolib.platform.util.buildItem

object EditHome {

    fun Basic.addButton(player: Player, icon: Char, material: XMaterial, lang: String, questID: String = "null", targetID: String = "null", addList: List<String> = listOf(), action: ClickEvent.() -> Unit = {}) {
        addButton(player, icon, material, player.asLangTextList(lang), questID, targetID, addList, action)
    }

    fun Basic.addButton(player: Player, icon: Char, material: XMaterial, lang: List<String>, questID: String = "null", targetID: String = "null", addList: List<String> = listOf(), action: ClickEvent.() -> Unit = {}) {
        set(icon, buildItem(material) {
            name = "§f                                        "
            val m = mutableListOf<String>()
            lang.forEach {
                if (it == "__List__") {
                    m.addAll(addList)
                }else {
                    m.add(it)
                }
            }
            lore.addAll(player.evalStringList(m){
                it.rootFrame().variables().set("@QenQuestID", questID)
                it.rootFrame().variables().set("@QenTargetID", targetID)
            })
        }) {
            action()
        }
    }

    fun Basic.addButton(player: Player, icon: Int, material: XMaterial, lang: String, questID: String = "null", targetID: String = "null", action: ClickEvent.() -> Unit = {}) {
        set(icon, buildItem(material) {
            name = "§f                                        "
            lore.addAll(player.evalStringList(player.asLangTextList(lang)){
                it.rootFrame().variables().set("@QenQuestID", questID)
                it.rootFrame().variables().set("@QenTargetID", targetID)
            })
        }) {
            action()
        }
    }

    fun open(player: Player) {
        player.openMenu<Basic>(player.asLangText("EDIT_UI_HOME")) {
            rows(6)
            map("--------B", "", "--X-@-#")
            addButton(player, 'B', XMaterial.ARROW, "HOME_CLOSE_UI") {
                player.closeInventory()
            }
            addButton(player, 'X', XMaterial.BOOK, "EDIT_UI_ADD_QUEST") {
                EditQuest.addQuest(player)
            }
            addButton(player, '@', XMaterial.CHEST, "EDIT_UI_LIST_QUEST") {
                EditQuestList.open(player)
            }
            addButton(player, '#', XMaterial.BOOKSHELF, "EDIT_UI_QUEST_GROUP") {
                EditGroupList.open(player)
            }
        }
    }

    fun Linked<*>.pageItem(player: Player) {
        setPreviousPage(48) { _, _ ->
            buildItem(XMaterial.SLIME_BALL) {
                name = player.asLangText("PREVIOUS_PAGE")
            }
        }
        setNextPage(50) { _, _ ->
            buildItem(XMaterial.SLIME_BALL) {
                name = player.asLangText("NEXT_PAGE")
            }
        }
        set(49, buildItem(XMaterial.BARRIER) {
            name = player.asLangText("CLOSE_UI")
        }) {
            player.closeInventory()
        }
    }

}