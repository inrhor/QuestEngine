package cn.inrhor.questengine.common.editor.ui

import cn.inrhor.questengine.script.kether.evalStringList
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.module.ui.ClickEvent
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.asLangText
import taboolib.platform.util.asLangTextList
import taboolib.platform.util.buildItem

object EditHome {

    fun Basic.addButton(player: Player, icon: Char, material: Material, lang: String, questID: String = "null", addList: List<String> = listOf(), action: ClickEvent.() -> Unit = {}) {
        addButton(player, icon, material, player.asLangTextList(lang), questID, addList, action)
    }

    fun Basic.addButton(player: Player, icon: Char, material: Material, lang: List<String>, questID: String = "null", addList: List<String> = listOf(), action: ClickEvent.() -> Unit = {}) {
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
            })
        }) {
            action()
        }
    }

    fun Basic.addButton(player: Player, icon: Int, material: Material, lang: String, questID: String = "null", action: ClickEvent.() -> Unit = {}) {
        set(icon, buildItem(material) {
            name = "§f                                        "
            lore.addAll(player.evalStringList(player.asLangTextList(lang)){
                it.rootFrame().variables().set("@QenQuestID", questID)
            })
        }) {
            action()
        }
    }

    fun open(player: Player) {
        player.openMenu<Basic>(player.asLangText("EDIT_UI_HOME")) {
            rows(6)
            map("", "", "--X-@-#", "", "", "")
            addButton(player, 'X', Material.BOOK, "EDIT_UI_ADD_QUEST") {
                EditQuest.addQuest(player)
            }
            addButton(player, '@', Material.CHEST, "EDIT_UI_LIST_QUEST") {
                EditQuestList.open(player)
            }
            addButton(player, '#', Material.BOOKSHELF, "EDIT_UI_EDIT_QUEST") {

            }
        }
    }

}