package cn.inrhor.questengine.script.kether.expand.editor

import cn.inrhor.questengine.common.editor.EditorInner.editorInner
import cn.inrhor.questengine.common.editor.EditorList.editorInnerDesc
import cn.inrhor.questengine.common.editor.EditorList.editorListInner
import cn.inrhor.questengine.common.editor.EditorTime.editTime
import cn.inrhor.questengine.common.quest.manager.QuestManager
import taboolib.common.util.addSafely
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.nms.inputSign
import taboolib.platform.util.asLangText
import taboolib.platform.util.sendLang
import java.util.concurrent.CompletableFuture
import cn.inrhor.questengine.script.kether.frameVoid
import cn.inrhor.questengine.script.kether.player
import cn.inrhor.questengine.script.kether.selectInnerID
import cn.inrhor.questengine.script.kether.selectQuestID

class EditorInner(val ui: ActionEditor.InnerUi, vararg val variable: String, val page: Int = 0) : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val sender = frame.player()
        val questID = frame.selectQuestID()
        when (ui) {
            ActionEditor.InnerUi.LIST -> {
                sender.editorListInner(questID, page)
            }
            ActionEditor.InnerUi.DEL -> {
                QuestManager.delInner(questID, frame.selectInnerID())
                sender.editorListInner(questID)
            }
            ActionEditor.InnerUi.ADD -> {
                val quest = QuestManager.getQuestModule(questID)?: return frameVoid()
                sender.inputSign(arrayOf(sender.asLangText("EDITOR-PLEASE-INNER-ID"))) {
                    val innerID = it[1].replace(" ", "")
                    if (innerID == "" || quest.existInner(innerID)) {
                        sender.sendLang("INNER-ERROR-ID", questID, innerID)
                        return@inputSign
                    }
                    QuestManager.saveFile(questID, innerID, innerCreate = true)
                    sender.editorListInner(questID)
                }
            }
            ActionEditor.InnerUi.EDIT -> {
                val innerID = frame.selectInnerID()
                when (variable[0]) {
                    "name" -> {
                        sender.inputSign(arrayOf(sender.asLangText("EDITOR-EDIT-INNER-NAME-INPUT"))) {
                            val innerModule = QuestManager.getInnerQuestModule(questID, innerID)?: return@inputSign
                            innerModule.name = it[1]
                            QuestManager.saveFile(questID, innerID)
                            sender.editorInner(questID, innerID)
                        }
                    }
                    "time" -> {
                        sender.editTime(questID, innerID)
                    }
                    "desc" -> {
                        sender.editorInnerDesc(questID, innerID)
                    }
                    else -> {
                        sender.editorInner(questID, innerID)
                    }
                }
            }
            ActionEditor.InnerUi.CHANGE -> {
                val innerID = frame.selectInnerID()
                val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return frameVoid()
                when (variable[0]) {
                    "desc" -> {
                        when (variable[1]) {
                            "add" -> {
                                sender.inputSign {
                                    val list = inner.description.toMutableList()
                                    var str = ""
                                    it.forEach { e -> str+=e }
                                    val index = if (variable[2]=="{head}") 0 else variable[2].toInt()+1
                                    list.addSafely(index, str, "")
                                    inner.description = list
                                    QuestManager.saveFile(questID, innerID)
                                    sender.editorInnerDesc(questID, innerID)
                                }
                            }
                            "del" -> {
                                val list = inner.description.toMutableList()
                                list.removeAt(variable[2].toInt())
                                inner.description = list
                                QuestManager.saveFile(questID, innerID)
                                sender.editorInnerDesc(questID, innerID)
                            }
                        }
                    }
                }
            }
        }
        return frameVoid()
    }
}