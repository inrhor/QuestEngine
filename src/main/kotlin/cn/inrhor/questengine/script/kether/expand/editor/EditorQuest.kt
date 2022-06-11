package cn.inrhor.questengine.script.kether.expand.editor

import cn.inrhor.questengine.script.kether.frameVoid
import cn.inrhor.questengine.common.editor.EditorHome.editorHomeQuest
import cn.inrhor.questengine.common.editor.EditorList.editorListQuest
import cn.inrhor.questengine.common.editor.EditorQuest.editorQuest
import cn.inrhor.questengine.common.quest.enum.ModeType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.manager.QuestManager.delQuestFile
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager.saveFile
import cn.inrhor.questengine.common.quest.manager.QuestManager.saveQuestFile
import cn.inrhor.questengine.script.kether.player
import cn.inrhor.questengine.script.kether.selectQuestID
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.nms.inputSign
import taboolib.platform.util.asLangText
import taboolib.platform.util.sendLang
import java.util.concurrent.CompletableFuture

class EditorQuest(val ui: ActionEditor.QuestUi, vararg val variable: String, val page: Int = 0) : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val sender = frame.player()
        when (ui) {
            ActionEditor.QuestUi.LIST -> sender.editorListQuest(page)
            ActionEditor.QuestUi.ADD -> {
                sender.inputSign(arrayOf(sender.asLangText("EDITOR-PLEASE-QUEST-ID"))) {
                    val questID = it[1].replace(" ", "")
                    if (questID == "" || QuestManager.getQuestMap().containsKey(questID)) {
                        sender.sendLang("QUEST-ERROR-ID", questID)
                        return@inputSign
                    }
                    questID.saveQuestFile()
                    sender.editorQuest(questID)
                }
            }
            ActionEditor.QuestUi.DEL -> {
                frame.selectQuestID().delQuestFile()
                sender.editorListQuest()
            }
            ActionEditor.QuestUi.EDIT -> {
                val questID = frame.selectQuestID()
                val quest = questID.getQuestFrame()
                when (variable[0]) {
                    "name" -> {
                        sender.inputSign(arrayOf(sender.asLangText("", "EDITOR-EDIT-QUEST-NAME-INPUT"))) {
                            quest.name = it[0]
                            quest.saveFile()
                            sender.editorQuest(questID)
                        }
                    }
                    "note" -> {
                        //
                    }
                    "sort" -> {
                        sender.inputSign(arrayOf(sender.asLangText("","EDITOR-PLEASE-SORT"))) {
                            quest.group.sort = it[0]
                            quest.saveFile()
                            sender.editorQuest(questID)
                        }
                    }
                    "modetype" -> {
                        val mode = quest.mode
                        mode.type =
                            if (mode.type == ModeType.PERSONAL) ModeType.COLLABORATION else ModeType.PERSONAL
                        quest.saveFile()
                        sender.editorQuest(questID)
                    }
                    "modeamount" -> {
                        sender.inputSign(arrayOf("",sender.asLangText("NUMBER-INPUT"))) {
                            try {
                                quest.mode.amount = it[0].toInt()
                            } catch (ex: Exception) {
                                quest.mode.amount = -1
                            }
                            quest.saveFile()
                            sender.editorQuest(questID)
                        }
                    }
                    "sharedata" -> {
                        val mode = quest.mode
                        mode.shareData = !mode.shareData
                        quest.saveFile()
                        sender.editorQuest(questID)
                    }
                    "acceptauto" -> {
                        val accept = quest.accept
                        accept.auto = !accept.auto
                        quest.saveFile()
                        sender.editorQuest(questID)
                    }
                    "control" -> {
                        //
                    }
                    "target" -> {
                        //
                    }
                    "time" -> {
                        //
                    }
                    else -> {
                        sender.editorQuest(frame.selectQuestID())
                    }
                }
            }
            ActionEditor.QuestUi.CHANGE -> {
                val questID = frame.selectQuestID()
                val quest = questID.getQuestFrame()
                val change = variable[1]
                when (variable[0]) {
                    "note" -> {
                        when (change) {
                            "del" -> {
                                //
                            }
                            "add" -> {
                                sender.inputSign(arrayOf(sender.asLangText("EDITOR-PLEASE-EVAL"))) {
                                    /*val con = quest.accept.condition
                                    val list = con.newLineList()
                                    val index = if (variable[2]=="{head}") 0 else variable[2].toInt()+1
                                    list.addSafely(index, it[1], "")
                                    quest.accept.condition = list.joinToString("\n")
                                    quest.saveFile()
                                    sender.editorAcceptCondition(questID)*/
                                }
                            }
                        }
                    }
                    "groupnote" -> {
                        when (change) {
                            //
                        }
                    }
                }
            }
            else -> sender.editorHomeQuest()
        }
        return frameVoid()
    }
}