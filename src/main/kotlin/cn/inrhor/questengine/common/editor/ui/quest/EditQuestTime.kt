package cn.inrhor.questengine.common.editor.ui.quest

import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.api.quest.TimeAddon
import cn.inrhor.questengine.common.editor.ui.EditHome.addButton
import cn.inrhor.questengine.common.editor.ui.EditQuest
import cn.inrhor.questengine.common.quest.manager.QuestManager.saveFile
import cn.inrhor.questengine.utlis.Input.inputBook
import cn.inrhor.questengine.utlis.lang
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.asLangText
import taboolib.platform.util.asLangTextList

object EditQuestTime {

    fun open(player: Player, questFrame: QuestFrame) {
        val id = questFrame.id
        val time = questFrame.time
        player.openMenu<Basic>(player.asLangText("EDIT_UI_QUEST_TIME")) {
            rows(6)
            map("--------B", "--TDR")
            addButton(player, 'B', XMaterial.ARROW, "EDIT_BACK_QUEST_EDIT", id) {
                EditQuest.openEdit(player, questFrame)
            }
            addButton(player, 'T', XMaterial.LEAD,
                player.asLangTextList("EDIT_QUEST_TIME_TYPE", time.type.lang(player)), id) {
                openType(player, questFrame)
            }
            addButton(player, 'D', XMaterial.COMPASS,
                player.asLangTextList("EDIT_QUEST_TIME_DURATION", time.langTime(player), time.duration), id) {
                editDuration(player, questFrame)
            }
            addButton(player, 'R', XMaterial.FISHING_ROD,
                player.asLangTextList("EDIT_QUEST_TIME_RESET", time.reset.lang(player)), id) {
                questFrame.time.reset = !questFrame.time.reset
                questFrame.saveFile()
                open(player, questFrame)
            }
        }
    }

    fun openType(player: Player, questFrame: QuestFrame) {
        val id = questFrame.id
        player.openMenu<Basic>(player.asLangText("EDIT_UI_QUEST_TIME_TYPE")) {
            rows(6)
            map("--------B", "--ADWMY", "--C")
            addButton(player, 'B', XMaterial.ARROW, "EDIT_BACK_TIME_EDIT", id) {
                open(player, questFrame)
            }
            addButton(player, 'A', XMaterial.GREEN_WOOL, "EDIT_QUEST_TIME_ALWAYS", id) {
                questFrame.time.type = TimeAddon.Type.ALWAYS
                questFrame.time.duration = ""
                questFrame.saveFile()
                open(player, questFrame)
            }
            addButton(player, 'D', XMaterial.LIGHT_BLUE_WOOL, "EDIT_QUEST_TIME_DAY", id) {
                questFrame.time.type = TimeAddon.Type.DAY
                questFrame.time.duration = ""
                editDuration(player, questFrame)
            }
            addButton(player, 'W', XMaterial.ORANGE_WOOL, "EDIT_QUEST_TIME_WEEKLY", id) {
                questFrame.time.type = TimeAddon.Type.WEEKLY
                questFrame.time.duration = ""
                editDuration(player, questFrame)
            }
            addButton(player, 'M', XMaterial.YELLOW_WOOL, "EDIT_QUEST_TIME_MONTHLY", id) {
                questFrame.time.type = TimeAddon.Type.MONTHLY
                questFrame.time.duration = ""
                editDuration(player, questFrame)
            }
            addButton(player, 'Y', XMaterial.RED_WOOL, "EDIT_QUEST_TIME_YEARLY", id) {
                questFrame.time.type = TimeAddon.Type.YEARLY
                questFrame.time.duration = ""
                editDuration(player, questFrame)
            }
            addButton(player, 'C', XMaterial.PINK_WOOL, "EDIT_QUEST_TIME_CUSTOM", id) {
                questFrame.time.type = TimeAddon.Type.CUSTOM
                questFrame.time.duration = ""
                editDuration(player, questFrame)
            }
        }
    }

    private fun editDuration(player: Player, questFrame: QuestFrame) {
        when (questFrame.time.type) {
            TimeAddon.Type.DAY -> editDay(player, questFrame)
            TimeAddon.Type.WEEKLY -> editWeek(player, questFrame)
            TimeAddon.Type.MONTHLY -> editMonth(player, questFrame)
            TimeAddon.Type.YEARLY -> editYear(player, questFrame)
            TimeAddon.Type.CUSTOM -> editCustom(player, questFrame)
            TimeAddon.Type.ALWAYS -> return
        }
        player.closeInventory()
    }

    private fun editDay(player: Player, questFrame: QuestFrame, startTime: String = "", endTime: String = "") {
        val lang = player.asLangText("EDIT_BOOK_TIME_DURATION")
        player.inputBook(lang, true,
            player.asLangTextList("EDIT_INPUT_TIME_START")) { s ->
            submit(delay = 2) {
                player.inputBook(lang, true,
                    player.asLangTextList("EDIT_INPUT_TIME_END")) { e ->
                    questFrame.time.duration = "$startTime${s[1]}>$endTime${e[1]}"
                    questFrame.saveFile()
                    open(player, questFrame)
                }
            }
        }
    }

    private fun editMonth(player: Player, questFrame: QuestFrame, m: String = "MONTH") {
        val lang = player.asLangText("EDIT_BOOK_TIME_DURATION")
        player.inputBook(lang, true,
            player.asLangTextList("EDIT_INPUT_${m}_START")) { s ->
            submit(delay = 2) {
                player.inputBook(lang, true,
                    player.asLangTextList("EDIT_INPUT_${m}_END")) { e ->
                    submit(delay = 2) {
                        editDay(player, questFrame, s[1]+",", e[1]+",")
                    }
                }
            }
        }
    }

    private fun editWeek(player: Player, questFrame: QuestFrame) {
        editMonth(player, questFrame, "WEEK")
    }

    private fun editYear(player: Player, questFrame: QuestFrame) {
        editMonth(player, questFrame, "YEAR")
    }

    private fun editCustom(player: Player, questFrame: QuestFrame) {
        val lang = player.asLangText("EDIT_BOOK_TIME_DURATION")
        player.inputBook(
            lang, true,
            player.asLangTextList("EDIT_INPUT_TIME_CUSTOM")) {
            questFrame.time.duration = it[1]
            questFrame.saveFile()
        }
    }

}