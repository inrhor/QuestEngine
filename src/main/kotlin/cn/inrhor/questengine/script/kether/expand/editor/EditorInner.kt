package cn.inrhor.questengine.script.kether.expand.editor

import cn.inrhor.questengine.common.edit.EditorInner.editorInner
import cn.inrhor.questengine.common.edit.EditorList.editorInnerDesc
import cn.inrhor.questengine.common.edit.EditorList.editorListInner
import cn.inrhor.questengine.common.edit.EditorList.editorNextInner
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.common.util.setSafely
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.script
import taboolib.module.nms.inputSign
import taboolib.platform.util.asLangText
import java.util.concurrent.CompletableFuture

class EditorInner(val ui: ActionEditor.InnerUi, val questID: String = "", val innerID: String = "", val meta: String = "", val change: String = "", val page: Int = 0) : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        return frame.run<Void?>().thenAccept {
            val sender = (frame.script().sender as? ProxyPlayer ?: error("unknown player")).cast<Player>()
            when (ui) {
                ActionEditor.InnerUi.LIST -> {
                    sender.editorListInner(questID, page)
                }
                ActionEditor.InnerUi.DEL -> {
                    QuestManager.delInner(questID, innerID)
                    sender.editorListInner(questID)
                }
                ActionEditor.InnerUi.EDIT -> {
                    when (meta) {
                        "name" -> {
                            sender.inputSign(arrayOf(sender.asLangText("EDITOR-EDIT-INNER-NAME-INPUT"))) {
                                val innerModule = QuestManager.getInnerQuestModule(questID, innerID)?: return@inputSign
                                innerModule.name = it[1]
                                QuestManager.saveFile(questID, innerID)
                                sender.editorInner(questID, innerID)
                            }
                        }
                        "nextinner" -> {
                            sender.editorNextInner(questID, innerID, page)
                        }
                        "desc" -> {
                            sender.editorInnerDesc(questID, innerID)
                        }
                    }
                }
                ActionEditor.InnerUi.CHANGE -> {
                    val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return@thenAccept
                    when (meta) {
                        "add" -> {
                            sender.inputSign {
                                val list = inner.description.toMutableList()
                                list.setSafely(change.toInt(), it[1], "")
                                inner.description = list
                                QuestManager.saveFile(questID, innerID)
                                sender.editorInnerDesc(questID, innerID)
                            }
                        }
                        "del" -> {
                            val list = inner.description.toMutableList()
                            list.removeAt(change.toInt())
                            inner.description = list
                            QuestManager.saveFile(questID, innerID)
                            sender.editorInnerDesc(questID, innerID)
                        }
                    }
                }
            }
        }
    }
}