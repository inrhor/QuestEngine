package cn.inrhor.questengine.script.kether.expand.editor

import cn.inrhor.questengine.script.kether.frameVoid
import cn.inrhor.questengine.common.editor.EditorHome.editorHomeQuest
import cn.inrhor.questengine.common.editor.EditorList.editorAcceptCondition
import cn.inrhor.questengine.common.editor.EditorList.editorFailCondition
import cn.inrhor.questengine.common.editor.EditorList.editorFailScript
import cn.inrhor.questengine.common.editor.EditorList.editorListQuest
import cn.inrhor.questengine.common.editor.EditorList.editorStartInner
import cn.inrhor.questengine.common.editor.EditorQuest.editorQuest
import cn.inrhor.questengine.common.quest.ModeType
import cn.inrhor.questengine.common.quest.manager.QuestManager
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
                    if (questID == "" || QuestManager.questMap.containsKey(questID)) {
                        sender.sendLang("QUEST-ERROR-ID", questID)
                        return@inputSign
                    }
                    QuestManager.saveFile(questID, create = true)
                    sender.editorQuest(questID)
                }
            }
            ActionEditor.QuestUi.DEL -> {
                QuestManager.delQuest(frame.selectQuestID())
                sender.editorListQuest()
            }
            ActionEditor.QuestUi.EDIT -> {
                when (variable[0]) {
                    "name" -> {
                        val questID = frame.selectQuestID()
                        val questModule = QuestManager.getQuestModule(questID) ?: return frameVoid()
                        sender.inputSign(arrayOf(sender.asLangText("EDITOR-EDIT-QUEST-NAME-INPUT"))) {
                            questModule.name = it[1]
                            QuestManager.saveFile(questID)
                            sender.editorQuest(questID)
                        }
                    }
                    "start" -> {
                        sender.editorStartInner(frame.selectQuestID())
                    }
                    "sort" -> {
                        val questID = frame.selectQuestID()
                        val questModule = QuestManager.getQuestModule(questID) ?: return frameVoid()
                        sender.inputSign(arrayOf(sender.asLangText("EDITOR-PLEASE-SORT"))) {
                            questModule.sort = it[1]
                            QuestManager.saveFile(questID)
                            sender.editorQuest(questID)
                        }
                    }
                    "modetype" -> {
                        val questID = frame.selectQuestID()
                        val questModule = QuestManager.getQuestModule(questID) ?: return frameVoid()
                        val mode = questModule.mode
                        mode.type =
                            if (mode.type == ModeType.PERSONAL) ModeType.COLLABORATION else ModeType.PERSONAL
                        QuestManager.saveFile(questID)
                        sender.editorQuest(questID)
                    }
                    "modeamount" -> {
                        val questID = frame.selectQuestID()
                        val questModule = QuestManager.getQuestModule(questID) ?: return frameVoid()
                        sender.inputSign(arrayOf(sender.asLangText("NUMBER-INPUT"))) {
                            try {
                                questModule.mode.amount = it[1].toInt()
                            } catch (ex: Exception) {
                                questModule.mode.amount = -1
                            }
                            QuestManager.saveFile(questID)
                            sender.editorQuest(questID)
                        }
                    }
                    "sharedata" -> {
                        val questID = frame.selectQuestID()
                        val questModule = QuestManager.getQuestModule(questID) ?: return frameVoid()
                        val mode = questModule.mode
                        mode.shareData = !mode.shareData
                        QuestManager.saveFile(questID)
                        sender.editorQuest(questID)
                    }
                    "acceptway" -> {
                        val questID = frame.selectQuestID()
                        val questModule = QuestManager.getQuestModule(questID) ?: return frameVoid()
                        val accept = questModule.accept
                        accept.way = if (accept.way == "auto") "" else "auto"
                        QuestManager.saveFile(questID)
                        sender.editorQuest(questID)
                    }
                    "maxquantity" -> {
                        val questID = frame.selectQuestID()
                        val questModule = QuestManager.getQuestModule(questID) ?: return frameVoid()
                        sender.inputSign(arrayOf(sender.asLangText("NUMBER-INPUT"))) {
                            try {
                                questModule.accept.maxQuantity = it[1].toInt()
                            } catch (ex: Exception) {
                                questModule.accept.maxQuantity = 1
                            }
                            QuestManager.saveFile(questID)
                            sender.editorQuest(questID)
                        }
                    }
                    "acceptcondition" -> {
                        sender.editorAcceptCondition(frame.selectQuestID(), page)
                    }
                    "failurecondition" -> {
                        sender.editorFailCondition(frame.selectQuestID(), page)
                    }
                    "failurescript" -> {
                        sender.editorFailScript(frame.selectQuestID(), page)
                    }
                    else -> {
                        sender.editorQuest(frame.selectQuestID())
                    }
                }
            }
            ActionEditor.QuestUi.CHANGE -> {
                val questID = frame.selectQuestID()
                val questModule = QuestManager.getQuestModule(questID)?: return frameVoid()
                val change = variable[1]
                when (variable[0]) {
                    "start" -> {
                        questModule.startInnerQuestID = change
                        sender.editorStartInner(questID)
                        QuestManager.saveFile(questID, change)
                    }
                    "acceptcondition" -> {
                        questModule.accept.delCondition(change.toInt())
                        QuestManager.saveFile(questID)
                        sender.editorAcceptCondition(questID)
                    }
                    "failcondition" -> {
                        questModule.failure.delCondition(change.toInt())
                        QuestManager.saveFile(questID)
                        sender.editorAcceptCondition(questID)
                    }
                    "failscript" -> {
                        questModule.failure.delScript(change.toInt())
                        QuestManager.saveFile(questID)
                        sender.editorAcceptCondition(questID)
                    }
                }
            }
            else -> sender.editorHomeQuest()
        }
        return frameVoid()
    }
}