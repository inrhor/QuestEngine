package cn.inrhor.questengine.script.kether.expand.editor

import cn.inrhor.questengine.common.edit.EditorHome.editorHome
import cn.inrhor.questengine.common.edit.EditorList.editorAcceptCondition
import cn.inrhor.questengine.common.edit.EditorList.editorFailCondition
import cn.inrhor.questengine.common.edit.EditorList.editorFailScript
import cn.inrhor.questengine.common.edit.EditorList.editorListQuest
import cn.inrhor.questengine.common.edit.EditorList.editorStartInner
import cn.inrhor.questengine.common.edit.EditorQuest.editorQuest
import cn.inrhor.questengine.common.quest.ModeType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.script
import taboolib.module.nms.inputSign
import taboolib.platform.util.asLangText
import taboolib.platform.util.sendLang
import java.util.concurrent.CompletableFuture

class EditorQuest(val ui: ActionEditor.QuestUi, val questID: String = "", val meta: String = "", val change: String = "", val page: Int = 0) : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        return frame.run<Void?>().thenAccept {
            val sender = (frame.script().sender as? ProxyPlayer ?: error("unknown player")).cast<Player>()
            when (ui) {
                ActionEditor.QuestUi.LIST -> sender.editorListQuest(page)
                ActionEditor.QuestUi.ADD -> run {
                    sender.inputSign(arrayOf(sender.asLangText("EDITOR-PLEASE-QUEST-ID"))) {
                        val questID = it[1].replace(" ", "")
                        if (questID == "" || QuestManager.questMap.containsKey(questID)) {
                            sender.sendLang("QUEST-ERROR-ID")
                            return@inputSign
                        }
                        QuestManager.saveFile(questID, create = true)
                        sender.editorQuest(questID)
                    }
                }
                ActionEditor.QuestUi.DEL -> run {
                    QuestManager.delQuest(questID)
                    sender.editorListQuest()
                }
                ActionEditor.QuestUi.EDIT -> run {
                    when (meta) {
                        "name" -> {
                            val questModule = QuestManager.getQuestModule(questID) ?: return@thenAccept
                            sender.inputSign(arrayOf(sender.asLangText("EDITOR-EDIT-QUEST-NAME-INPUT"))) {
                                questModule.name = it[1]
                                QuestManager.saveFile(questID)
                                sender.editorQuest(questID)
                            }
                        }
                        "start" -> {
                            sender.editorStartInner(questID)
                        }
                        "sort" -> {
                            val questModule = QuestManager.getQuestModule(questID) ?: return@thenAccept
                            sender.inputSign(arrayOf(sender.asLangText("EDITOR-PLEASE-SORT"))) {
                                questModule.sort = it[1]
                                QuestManager.saveFile(questID)
                                sender.editorQuest(questID)
                            }
                        }
                        "modetype" -> {
                            val questModule = QuestManager.getQuestModule(questID) ?: return@thenAccept
                            val mode = questModule.mode
                            mode.type =
                                if (mode.type == ModeType.PERSONAL) ModeType.COLLABORATION else ModeType.PERSONAL
                            QuestManager.saveFile(questID)
                            sender.editorQuest(questID)
                        }
                        "modeamount" -> {
                            val questModule = QuestManager.getQuestModule(questID) ?: return@thenAccept
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
                            val questModule = QuestManager.getQuestModule(questID) ?: return@thenAccept
                            val mode = questModule.mode
                            mode.shareData = !mode.shareData
                            QuestManager.saveFile(questID)
                            sender.editorQuest(questID)
                        }
                        "acceptway" -> {
                            val questModule = QuestManager.getQuestModule(questID) ?: return@thenAccept
                            val accept = questModule.accept
                            accept.way = if (accept.way == "auto") "" else "auto"
                            QuestManager.saveFile(questID)
                            sender.editorQuest(questID)
                        }
                        "maxquantity" -> {
                            val questModule = QuestManager.getQuestModule(questID) ?: return@thenAccept
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
                            sender.editorAcceptCondition(questID, page)
                        }
                        "failurecondition" -> {
                            sender.editorFailCondition(questID, page)
                        }
                        "failurescript" -> {
                            sender.editorFailScript(questID, page)
                        }
                    }
                }
                ActionEditor.QuestUi.CHANGE -> {
                    val questModule = QuestManager.getQuestModule(questID)?: return@thenAccept
                    when (meta) {
                        "start" -> {
                            questModule.startInnerQuestID = change
                            sender.editorStartInner(questID)
                            QuestManager.saveFile(questID, change)
                        }
                        "acceptcondition" -> {
                            questModule.accept.condition.removeAt(change.toInt())
                            QuestManager.saveFile(questID)
                            sender.editorAcceptCondition(questID)
                        }
                        "failcondition" -> {
                            questModule.failure.condition.removeAt(change.toInt())
                            QuestManager.saveFile(questID)
                            sender.editorAcceptCondition(questID)
                        }
                        "failscript" -> {
                            questModule.failure.script.removeAt(change.toInt())
                            QuestManager.saveFile(questID)
                            sender.editorAcceptCondition(questID)
                        }
                    }
                }
                else -> sender.editorHome()
            }
        }
    }
}