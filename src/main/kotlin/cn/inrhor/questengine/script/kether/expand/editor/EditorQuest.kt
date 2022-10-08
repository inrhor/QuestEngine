package cn.inrhor.questengine.script.kether.expand.editor

import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.script.kether.frameVoid
import cn.inrhor.questengine.common.editor.EditorHome.editorHomeQuest
import cn.inrhor.questengine.common.editor.EditorList.editGroupNote
import cn.inrhor.questengine.common.editor.EditorList.editQuestNote
import cn.inrhor.questengine.common.editor.EditorList.editorAcceptCondition
import cn.inrhor.questengine.common.editor.EditorList.editorListQuest
import cn.inrhor.questengine.common.editor.EditorList.editorTargetList
import cn.inrhor.questengine.common.editor.EditorQuest.editorQuest
import cn.inrhor.questengine.common.editor.EditorTime.editTime
import cn.inrhor.questengine.common.quest.enum.ModeType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.manager.QuestManager.delQuestFile
import cn.inrhor.questengine.common.quest.manager.QuestManager.existQuestFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager.register
import cn.inrhor.questengine.common.quest.manager.QuestManager.saveFile
import cn.inrhor.questengine.script.kether.player
import cn.inrhor.questengine.script.kether.selectQuestID
import taboolib.common.util.addSafely
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
                sender.inputSign(arrayOf("",sender.asLangText("EDITOR-PLEASE-QUEST-ID"))) {
                    val questID = it[0].replace(" ", "")
                    if (questID == "" || QuestManager.getQuestMap().containsKey(questID)) {
                        sender.sendLang("QUEST-ERROR-ID", questID)
                        return@inputSign
                    }
                    val questFrame = QuestFrame(id = questID, path = "plugins\\QuestEngine\\space\\quest\\$questID.yml")
                    questFrame.register()
                    questFrame.saveFile(true)
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
                        sender.inputSign(arrayOf("",sender.asLangText("EDITOR-EDIT-QUEST-NAME-INPUT"))) {
                            quest.name = it[0]
                            quest.saveFile()
                            sender.editorQuest(questID)
                        }
                    }
                    "note" -> {
                        sender.editQuestNote(quest.note, questID, page)
                    }
                    "groupextends" -> {
                        sender.inputSign(arrayOf("",sender.asLangText("EDITOR-PLEASE-QUEST-ID"))) {
                            quest.group.extends = it[0]
                            val ext = quest.group.extends
                            if (ext.existQuestFrame()) {
                                quest.group = ext.getQuestFrame().group
                            }
                            quest.saveFile()
                            sender.editorQuest(questID)
                        }
                    }
                    "groupnumber" -> {
                        sender.inputSign(arrayOf("",sender.asLangText("EDITOR-PLEASE-GROUP-NUMBER"))) {
                            quest.group.number = it[0]
                            quest.saveFile()
                            sender.editorQuest(questID)
                        }
                    }
                    "groupnote" -> {
                        sender.editGroupNote(quest.group.note, questID, page)
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
                        if (accept.auto) {
                            QuestManager.autoQuestMap.remove(questID)
                        }
                        accept.auto = !accept.auto
                        quest.saveFile()
                        sender.editorQuest(questID)
                    }
                    "acceptcondition" -> {
                        sender.editorAcceptCondition(questID, page)
                    }
                    "control" -> {
                        //
                    }
                    "target" -> {
                        sender.editorTargetList(questID, page)
                    }
                    "time" -> {
                        sender.editTime(questID)
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
                                val m = quest.note.toMutableList()
                                m.removeAt(variable[2].toInt())
                                quest.note = m
                                quest.saveFile()
                                sender.editQuestNote(quest.note, questID)
                            }
                            "add" -> {
                                sender.inputSign(arrayOf(sender.asLangText("EDITOR-PLEASE-EVAL"))) {
                                    val index = if (variable[2]=="{head}") 0 else variable[2].toInt()+1
                                    val m = quest.note.toMutableList()
                                    m.addSafely(index, it[1]+it[2]+it[3], "")
                                    quest.note = m
                                    quest.saveFile()
                                    sender.editQuestNote(quest.note, questID)
                                }
                            }
                        }
                    }
                    "groupnote" -> {
                        when (change) {
                            "del" -> {
                                val m = quest.group.note.toMutableList()
                                m.removeAt(variable[2].toInt())
                                quest.group.note = m
                                quest.saveFile()
                                sender.editGroupNote(quest.group.note, questID)
                            }
                            "add" -> {
                                sender.inputSign(arrayOf(sender.asLangText("EDITOR-PLEASE-EVAL"))) {
                                    val index = if (variable[2]=="{head}") 0 else variable[2].toInt()+1
                                    val m = quest.group.note.toMutableList()
                                    m.addSafely(index, it[1]+it[2]+it[3], "")
                                    quest.group.note = m
                                    quest.saveFile()
                                    sender.editGroupNote(quest.group.note, questID)
                                }
                            }
                        }
                    }
                }
            }
            else -> sender.editorHomeQuest()
        }
        return frameVoid()
    }
}