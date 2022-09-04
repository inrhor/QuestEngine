package cn.inrhor.questengine.script.kether.expand.editor

import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.api.target.RegisterTarget
import cn.inrhor.questengine.common.editor.EditorList.editorNodeList
import cn.inrhor.questengine.common.editor.EditorList.editorTargetCondition
import cn.inrhor.questengine.common.editor.EditorList.editorTargetList
import cn.inrhor.questengine.common.editor.EditorList.selectTargetList
import cn.inrhor.questengine.common.editor.EditorTarget.editorTarget
import cn.inrhor.questengine.common.editor.EditorTarget.editorTargetNode
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager.getTargetFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager.saveFile
import cn.inrhor.questengine.common.quest.manager.QuestManager.saveQuestFile
import cn.inrhor.questengine.script.kether.*
import cn.inrhor.questengine.utlis.UtilString
import cn.inrhor.questengine.utlis.newLineList
import cn.inrhor.questengine.utlis.removeAt
import taboolib.common.util.addSafely
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.nms.inputSign
import taboolib.platform.util.asLangText
import taboolib.platform.util.sendLang
import java.util.concurrent.CompletableFuture

class EditorTarget(val ui: ActionEditor.TargetUi, vararg val variable: String, val page: Int = 0) : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val sender = frame.player()
        val questID = frame.selectQuestID()
        when (ui) {
            ActionEditor.TargetUi.CHANGE -> {
                val targetID = frame.selectTargetID()
                when (variable[0]) {
                    "node" -> {
                        val target = targetID.getTargetFrame(questID)
                        val node = variable[1]
                        val i = variable[3]
                        val meta = target.nodeMeta(node)?: mutableListOf()
                        if (variable[2]=="del") {
                            meta.removeAt(i.toInt())
                            target.reloadNode(node, meta)
                            questID.saveQuestFile()
                            sender.editorNodeList(questID, target, node)
                        }else {
                            sender.inputSign(arrayOf(sender.asLangText("EDITOR-PLEASE-NODE-CONTENT"))) {
                                val index = if (i=="{head}") 0 else i.toInt()+1
                                meta.addSafely(index, it[1]+it[2]+it[3], "")
                                target.reloadNode(node, meta)
                                questID.saveQuestFile()
                                sender.editorNodeList(questID, target, node)
                            }
                        }
                    }
                    "name" -> {
                        val target = targetID.getTargetFrame(questID)
                        target.event = variable[1]
                        target.node = ""
                        questID.saveQuestFile()
                        sender.editorTarget(questID, targetID)
                    }
                    "condition" -> {
                        val target = targetID.getTargetFrame(questID)
                        val i = variable[2]
                        if (variable[1]=="del") {
                            target.condition = target.condition.removeAt(i.toInt())
                            questID.saveQuestFile()
                            sender.editorTargetCondition(questID, targetID, 0)
                        }else {
                            sender.inputSign(arrayOf(sender.asLangText("EDITOR-PLEASE-EVAL"))) {
                                val list = target.condition.newLineList()
                                val index = if (i=="{head}") 0 else i.toInt()+1
                                list.addSafely(index, it[1], "")
                                target.condition = list.joinToString("\n")
                                questID.saveQuestFile()
                                sender.editorTargetCondition(questID, targetID, 0)
                            }
                        }
                    }
                }
            }
            ActionEditor.TargetUi.LIST -> {
                sender.editorTargetList(questID, page)
            }
            ActionEditor.TargetUi.EDIT -> {
                val targetID = frame.selectTargetID()
                when (variable[0]) {
                    "name" -> {
                        sender.selectTargetList(questID, targetID)
                    }
                    "async" -> {
                        val target = targetID.getTargetFrame(questID)
                        val a = target.async
                        target.async = !a
                        sender.editorTarget(questID, targetID)
                        questID.saveQuestFile()
                    }
                    "condition" -> {
                        sender.editorTargetCondition(questID, targetID, page)
                    }
                    "node" -> {
                        val target = targetID.getTargetFrame(questID)
                        val node = RegisterTarget.getNode(target.event, variable[1])?: return frameVoid()
                        sender.editorTargetNode(questID, target, node)
                    }
                    else -> {
                        sender.editorTarget(questID, targetID)
                    }
                }
            }
            ActionEditor.TargetUi.DEL -> {
                val quest = questID.getQuestFrame()
                quest.delTarget(frame.selectTargetID())
                quest.saveFile()
                sender.editorTargetList(questID)
            }
            ActionEditor.TargetUi.ADD -> {
                sender.inputSign(arrayOf(sender.asLangText("EDITOR-PLEASE-TARGET-ID"))) {
                    val quest = questID.getQuestFrame()
                    val id = it[1]
                    if (quest.existTargetID(id) || id.isEmpty()) {
                        sender.sendLang("EXIST-TARGET-ID", UtilString.pluginTag, id)
                        return@inputSign
                    }
                    val target = TargetFrame()
                    target.id = id
                    quest.target.add(target)
                    quest.saveFile()
                    sender.selectTargetList(questID, id)
                }
            }
            ActionEditor.TargetUi.SEL -> {
                val targetID = frame.selectTargetID()
                when (variable[0]) {
                    "node" -> {
                        val target = targetID.getTargetFrame(questID)
                        val node = RegisterTarget.getNode(target.event, variable[1])?: return frameVoid()
                        sender.editorTargetNode(questID, target, node)
                    }
                    "list" -> {
                        sender.selectTargetList(questID, targetID, page)
                    }
                }
            }
        }
        return frameVoid()
    }
}