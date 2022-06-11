package cn.inrhor.questengine.script.kether.expand.editor

import cn.inrhor.questengine.api.quest.TimeAddon
import cn.inrhor.questengine.common.editor.EditorTime.editTime
import cn.inrhor.questengine.common.editor.EditorTime.selectTimeType
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager.saveFile
import cn.inrhor.questengine.script.kether.*
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.nms.inputSign
import taboolib.platform.util.asLangText
import taboolib.platform.util.asLangTextList
import java.util.concurrent.CompletableFuture

class EditorTime(val ui: ActionEditor.TimeUi, vararg val variable: String) : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val sender = frame.player()
        val questID = frame.selectQuestID()
        when (ui) {
            ActionEditor.TimeUi.EDIT -> {
                when (variable[0].lowercase()) {
                    "type" -> {
                        sender.selectTimeType(questID)
                    }
                    "day" -> {
                        val quest = questID.getQuestFrame()
                        sender.inputSign(sender.asLangTextList("","EDITOR-PLEASE-TIME",
                            sender.asLangText("EDITOR-TIME-FROM"),).toTypedArray()) {
                            sender.inputSign(sender.asLangTextList("","EDITOR-PLEASE-TIME",
                                sender.asLangText("EDITOR-TIME-END"),).toTypedArray()) { a ->
                                quest.time.duration = "${it[0]}>${a[0]}"
                                quest.saveFile()
                                sender.editTime(questID)
                            }
                        }
                    }
                    "weekly" -> {
                        val quest = questID.getQuestFrame()
                        sender.inputSign(sender.asLangTextList("","EDITOR-PLEASE-WEEK",
                            sender.asLangText("EDITOR-TIME-FROM")).toTypedArray()) {
                            val week1 = it[0].toInt()-1
                            sender.inputSign(sender.asLangTextList("","EDITOR-PLEASE-TIME",
                                sender.asLangText("EDITOR-TIME-FROM")).toTypedArray()) { a ->
                                sender.inputSign(sender.asLangTextList("","EDITOR-PLEASE-WEEK",
                                    sender.asLangText("EDITOR-TIME-END")).toTypedArray()) { b->
                                    val week2 = b[0].toInt()-1
                                    sender.inputSign(
                                        sender.asLangTextList(
                                            "EDITOR-PLEASE-TIME",
                                            sender.asLangText("EDITOR-TIME-END")
                                        ).toTypedArray()) { c ->
                                        quest.time.duration = "$week1,${a[0]}>$week2,${c[0]}"
                                        quest.saveFile()
                                        sender.editTime(questID)
                                    }
                                }
                            }
                        }
                    }
                    "monthly" -> {
                        val quest = questID.getQuestFrame()
                        sender.inputSign(sender.asLangTextList("","EDITOR-PLEASE-DAY",
                            sender.asLangText("EDITOR-TIME-FROM")).toTypedArray()) {
                            sender.inputSign(sender.asLangTextList("","EDITOR-PLEASE-TIME",
                                sender.asLangText("EDITOR-TIME-FROM")).toTypedArray()) { a ->
                                sender.inputSign(sender.asLangTextList("","EDITOR-PLEASE-DAY",
                                    sender.asLangText("EDITOR-TIME-END")).toTypedArray()) { b ->
                                    sender.inputSign(sender.asLangTextList("","EDITOR-PLEASE-TIME",
                                        sender.asLangText("EDITOR-TIME-END")).toTypedArray()) { c ->
                                        quest.time.duration = "${it[0]},${a[0]}>${b[0]},${c[0]}"
                                        quest.saveFile()
                                        sender.editTime(questID)
                                    }
                                }
                            }
                        }
                    }
                    "yearly" -> {
                        val quest = questID.getQuestFrame()
                        sender.inputSign(sender.asLangTextList("","EDITOR-PLEASE-MONTH",
                            sender.asLangText("EDITOR-TIME-FROM")).toTypedArray()) {
                            val m1 = it[0].toInt()-1
                            sender.inputSign(sender.asLangTextList("","EDITOR-PLEASE-DAY",
                                sender.asLangText("EDITOR-TIME-FROM")).toTypedArray()) { a->
                                sender.inputSign(
                                    sender.asLangTextList(
                                        "","EDITOR-PLEASE-TIME",
                                        sender.asLangText("EDITOR-TIME-FROM")
                                    ).toTypedArray()) { b ->
                                    sender.inputSign(sender.asLangTextList("","EDITOR-PLEASE-MONTH",
                                        sender.asLangText("EDITOR-TIME-END")).toTypedArray()) { c->
                                        val m2 = c[0].toInt() - 1
                                        sender.inputSign(
                                            sender.asLangTextList(
                                                "","EDITOR-PLEASE-DAY",
                                                sender.asLangText("EDITOR-TIME-END")
                                            ).toTypedArray()) { d ->
                                            sender.inputSign(
                                                sender.asLangTextList(
                                                    "","EDITOR-PLEASE-TIME",
                                                    sender.asLangText("EDITOR-TIME-END")
                                                ).toTypedArray()) { e ->
                                                quest.time.duration = "$m1,${a[0]},${b[0]}>$m2,${d[0]},${e[0]}"
                                                quest.saveFile()
                                                sender.editTime(questID)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    "custom" -> {
                        val quest = questID.getQuestFrame()
                        sender.inputSign(sender.asLangTextList("","EDITOR-PLEASE-TIME-CUSTOM").toTypedArray()) {
                            quest.time.duration = it[2]
                            quest.saveFile()
                            sender.editTime(questID)
                        }
                    }
                    else -> {
                        sender.editTime(questID)
                    }
                }
            }
            ActionEditor.TimeUi.CHANGE -> {
                val quest = questID.getQuestFrame()
                val time = quest.time
                time.type = TimeAddon.Type.valueOf(variable[1].uppercase())
                time.duration = ""
                if (time.type != TimeAddon.Type.ALWAYS) {
                    runEval(sender, "quest select $questID editor time in edit ${variable[0]}")
                }
                quest.saveFile()
                sender.selectTimeType(questID)
            }
        }
        return frameVoid()
    }
}