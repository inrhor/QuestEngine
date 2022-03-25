package cn.inrhor.questengine.command.editor

import cn.inrhor.questengine.common.edit.EditorHome.editorHomeQuest
import cn.inrhor.questengine.common.edit.EditorList.editorAcceptCondition
import cn.inrhor.questengine.common.edit.EditorList.editorFailCondition
import cn.inrhor.questengine.common.edit.EditorList.editorFailScript
import cn.inrhor.questengine.common.edit.EditorList.editorStartInner
import cn.inrhor.questengine.common.edit.EditorList.editorListQuest
import cn.inrhor.questengine.common.edit.EditorQuest.editorQuest
import cn.inrhor.questengine.common.quest.ModeType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.subCommand
import taboolib.module.nms.inputSign
import taboolib.platform.util.asLangText
import taboolib.platform.util.sendLang

internal object EditorQuestCommand {

    @CommandBody
    val home = subCommand {
        execute<Player> { sender, _, _ ->
            sender.editorHomeQuest()
        }
    }

    @CommandBody
    val list = subCommand {
        dynamic {
            execute<Player> { sender, _, argument ->
                val args = argument.split(" ")
                sender.editorListQuest(args[0].toInt())
            }
        }
        execute<Player> { sender, _, _ ->
            sender.editorListQuest()
        }
    }

    @CommandBody
    val add = subCommand {
        execute<Player> { sender, _, _ ->
            sender.inputSign(arrayOf(sender.asLangText("EDITOR-PLEASE-QUEST-ID"))) {
                val questID = it[1].replace(" ", "")
                if (questID.isEmpty() && QuestManager.questMap.containsKey(questID)) {
                    sender.sendLang("QUEST-ERROR-ID")
                    return@inputSign
                }
                QuestManager.saveFile(questID, create = true)
                sender.editorQuest(questID)
            }
        }
    }

    @CommandBody
    val del = subCommand {
        dynamic {
            execute<Player> { sender, _, argument ->
                val args = argument.split(" ")
                val questID = args[0]
                QuestManager.delQuest(questID)
                sender.editorListQuest()
            }
        }
    }

    @CommandBody
    val edit = subCommand {
        literal("name") {
            dynamic {
                execute<Player> { sender, _, argument ->
                    val questID = argument.split(" ")[0]
                    val questModule = QuestManager.getQuestModule(questID)?: return@execute
                    sender.inputSign(arrayOf(sender.asLangText("EDITOR-EDIT-QUEST-NAME-INPUT"))) {
                        questModule.name = it[1]
                        QuestManager.saveFile(questID)
                        sender.editorQuest(questID)
                    }
                }
            }
        }
        literal("start") {
            dynamic {
                execute<Player> { sender, _, argument ->
                    val questID = argument.split(" ")[0]
                    sender.editorStartInner(questID)
                }
            }
        }
        literal("sort") {
            dynamic {
                execute<Player> { sender, _, argument ->
                    val questID = argument.split(" ")[0]
                    val questModule = QuestManager.getQuestModule(questID)?: return@execute
                    sender.inputSign(arrayOf(sender.asLangText("EDITOR-PLEASE-SORT"))) {
                        questModule.sort = it[1]
                        QuestManager.saveFile(questID)
                        sender.editorQuest(questID)
                    }
                }
            }
        }
        literal("modetype") {
            dynamic {
                execute<Player> { sender, _, argument ->
                    val questID = argument.split(" ")[0]
                    val questModule = QuestManager.getQuestModule(questID)?: return@execute
                    val mode = questModule.mode
                    mode.type = if (mode.type == ModeType.PERSONAL) ModeType.COLLABORATION else ModeType.PERSONAL
                    QuestManager.saveFile(questID)
                    sender.editorQuest(questID)
                }
            }
        }
        literal("modeamount") {
            dynamic {
                execute<Player> { sender, _, argument ->
                    val questID = argument.split(" ")[0]
                    val questModule = QuestManager.getQuestModule(questID)?: return@execute
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
            }
        }
        literal("sharedata") {
            dynamic {
                execute<Player> { sender, _, argument ->
                    val questID = argument.split(" ")[0]
                    val questModule = QuestManager.getQuestModule(questID)?: return@execute
                    val mode = questModule.mode
                    mode.shareData = !mode.shareData
                    QuestManager.saveFile(questID)
                    sender.editorQuest(questID)
                }
            }
        }
        literal("acceptway") {
            dynamic {
                execute<Player> { sender, _, argument ->
                    val questID = argument.split(" ")[0]
                    val questModule = QuestManager.getQuestModule(questID)?: return@execute
                    val accept = questModule.accept
                    accept.way = if (accept.way == "auto") "" else "auto"
                    QuestManager.saveFile(questID)
                    sender.editorQuest(questID)
                }
            }
        }
        literal("maxquantity") {
            dynamic {
                execute<Player> { sender, _, argument ->
                    val questID = argument.split(" ")[0]
                    val questModule = QuestManager.getQuestModule(questID)?: return@execute
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
            }
        }
        literal("acceptcondition") {
            dynamic {
                dynamic {
                    execute<Player> { sender, content, argument ->
                        val questID = content.argument(-1)
                        val page = argument.split(" ")[0].toInt()
                        sender.editorAcceptCondition(questID, page)
                    }
                }
                execute<Player> { sender, _, argument ->
                    val questID = argument.split(" ")[0]
                    sender.editorAcceptCondition(questID)
                }
            }
        }
        literal("failurecondition") {
            dynamic {
                dynamic {
                    execute<Player> { sender, content, argument ->
                        val questID = content.argument(-1)
                        val page = argument.split(" ")[0].toInt()
                        sender.editorFailCondition(questID, page)
                    }
                }
                execute<Player> { sender, _, argument ->
                    val questID = argument.split(" ")[0]
                    sender.editorFailCondition(questID)
                }
            }
        }
        literal("failurescript") {
            dynamic {
                dynamic {
                    execute<Player> { sender, content, argument ->
                        val questID = content.argument(-1)
                        val page = argument.split(" ")[0].toInt()
                        sender.editorFailScript(questID, page)
                    }
                }
                execute<Player> { sender, _, argument ->
                    val questID = argument.split(" ")[0]
                    sender.editorFailScript(questID)
                }
            }
        }
        dynamic {
            execute<Player> { sender, _, argument ->
                val questID = argument.split(" ")[0]
                sender.editorQuest(questID)
            }
        }
    }

    @CommandBody
    val change = QuestChangeCommand

}