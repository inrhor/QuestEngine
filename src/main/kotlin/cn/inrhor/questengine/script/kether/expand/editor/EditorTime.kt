package cn.inrhor.questengine.script.kether.expand.editor

import cn.inrhor.questengine.api.quest.module.inner.TimeFrame
import cn.inrhor.questengine.common.editor.EditorTime.editTime
import cn.inrhor.questengine.common.editor.EditorTime.selectTimeType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.script.kether.runEval
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.script
import taboolib.module.nms.inputSign
import taboolib.platform.util.asLangText
import taboolib.platform.util.asLangTextList
import java.util.concurrent.CompletableFuture

class EditorTime(val ui: ActionEditor.TimeUi,
                 val questID: String = "", val innerID: String = "",
                 val meta: String = "", val change: String = "") : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val sender = (frame.script().sender as? ProxyPlayer ?: error("unknown player")).cast<Player>()
        when (ui) {
            ActionEditor.TimeUi.EDIT -> {
                when (meta.lowercase()) {
                    "type" -> {
                        sender.selectTimeType(questID, innerID)
                    }
                    "day" -> {
                        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return frameVoid()
                        sender.inputSign(sender.asLangTextList("EDITOR-PLEASE-TIME",
                            sender.asLangText("EDITOR-TIME-FROM"),).toTypedArray()) {
                            sender.inputSign(sender.asLangTextList("EDITOR-PLEASE-TIME",
                                sender.asLangText("EDITOR-TIME-END"),).toTypedArray()) { a ->
                                inner.time.duration = "${it[3]}>${a[3]}"
                                QuestManager.saveFile(questID, innerID)
                                sender.editTime(questID, innerID)
                            }
                        }
                    }
                    "weekly" -> {
                        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return frameVoid()
                        sender.inputSign(sender.asLangTextList("EDITOR-PLEASE-WEEK",
                            sender.asLangText("EDITOR-TIME-FROM")).toTypedArray()) {
                            val week1 = it[3].toInt()-1
                            sender.inputSign(sender.asLangTextList("EDITOR-PLEASE-TIME",
                                sender.asLangText("EDITOR-TIME-FROM")).toTypedArray()) { a ->
                                sender.inputSign(sender.asLangTextList("EDITOR-PLEASE-WEEK",
                                    sender.asLangText("EDITOR-TIME-END")).toTypedArray()) { b->
                                    val week2 = b[3].toInt()-1
                                    sender.inputSign(
                                        sender.asLangTextList(
                                            "EDITOR-PLEASE-TIME",
                                            sender.asLangText("EDITOR-TIME-END")
                                        ).toTypedArray()) { c ->
                                        inner.time.duration = "$week1,${a[3]}>$week2,${c[3]}"
                                        QuestManager.saveFile(questID, innerID)
                                        sender.editTime(questID, innerID)
                                    }
                                }
                            }
                        }
                    }
                    "monthly" -> {
                        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return frameVoid()
                        sender.inputSign(sender.asLangTextList("EDITOR-PLEASE-DAY",
                            sender.asLangText("EDITOR-TIME-FROM")).toTypedArray()) {
                            sender.inputSign(sender.asLangTextList("EDITOR-PLEASE-TIME",
                                sender.asLangText("EDITOR-TIME-FROM")).toTypedArray()) { a ->
                                sender.inputSign(sender.asLangTextList("EDITOR-PLEASE-DAY",
                                    sender.asLangText("EDITOR-TIME-END")).toTypedArray()) { b ->
                                    sender.inputSign(sender.asLangTextList("EDITOR-PLEASE-TIME",
                                        sender.asLangText("EDITOR-TIME-END")).toTypedArray()) { c ->
                                        inner.time.duration = "${it[3]},${a[3]}>${b[3]},${c[3]}"
                                        QuestManager.saveFile(questID, innerID)
                                        sender.editTime(questID, innerID)
                                    }
                                }
                            }
                        }
                    }
                    "yearly" -> {
                        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return frameVoid()
                        sender.inputSign(sender.asLangTextList("EDITOR-PLEASE-MONTH",
                            sender.asLangText("EDITOR-TIME-FROM")).toTypedArray()) {
                            val m1 = it[3].toInt()-1
                            sender.inputSign(sender.asLangTextList("EDITOR-PLEASE-DAY",
                                sender.asLangText("EDITOR-TIME-FROM")).toTypedArray()) { a->
                                sender.inputSign(
                                    sender.asLangTextList(
                                        "EDITOR-PLEASE-TIME",
                                        sender.asLangText("EDITOR-TIME-FROM")
                                    ).toTypedArray()) { b ->
                                    sender.inputSign(sender.asLangTextList("EDITOR-PLEASE-MONTH",
                                        sender.asLangText("EDITOR-TIME-END")).toTypedArray()) { c->
                                        val m2 = c[3].toInt() - 1
                                        sender.inputSign(
                                            sender.asLangTextList(
                                                "EDITOR-PLEASE-DAY",
                                                sender.asLangText("EDITOR-TIME-END")
                                            ).toTypedArray()) { d ->
                                            sender.inputSign(
                                                sender.asLangTextList(
                                                    "EDITOR-PLEASE-TIME",
                                                    sender.asLangText("EDITOR-TIME-END")
                                                ).toTypedArray()) { e ->
                                                inner.time.duration = "$m1,${a[3]},${b[3]}>$m2,${d[3]},${e[3]}"
                                                QuestManager.saveFile(questID, innerID)
                                                sender.editTime(questID, innerID)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    "custom" -> {
                        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return frameVoid()
                        sender.inputSign(sender.asLangTextList("EDITOR-PLEASE-TIME-CUSTOM").toTypedArray()) {
                            inner.time.duration = it[2]
                            QuestManager.saveFile(questID, innerID)
                            sender.editTime(questID, innerID)
                        }
                    }
                    else -> {
                        sender.editTime(questID, innerID)
                    }
                }
            }
            ActionEditor.TimeUi.CHANGE -> {
                val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return frameVoid()
                val time = inner.time
                time.type = TimeFrame.Type.valueOf(change.uppercase())
                time.duration = ""
                if (time.type != TimeFrame.Type.ALWAYS) {
                    runEval(sender, "editor time in edit $change select $questID $innerID")
                }
                QuestManager.saveFile(questID, innerID)
                sender.selectTimeType(questID, innerID)
            }
        }
        return frameVoid()
    }
}