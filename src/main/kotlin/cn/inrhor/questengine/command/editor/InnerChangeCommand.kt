package cn.inrhor.questengine.command.editor

import cn.inrhor.questengine.common.edit.EditorList.editorInnerDesc
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.subCommand
import taboolib.common.util.setSafely
import taboolib.module.nms.inputSign

object InnerChangeCommand {

    @CommandBody
    val desc = subCommand {
        literal("add") {
            dynamic {
                dynamic {
                    dynamic {
                        execute<Player> { sender, content, argument ->
                            val questID = content.argument(-2)
                            val innerID = content.argument(-1)
                            val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return@execute
                            val index = argument.split(" ")[0].toInt()
                            sender.inputSign {
                                val list = inner.description.toMutableList()
                                list.setSafely(index, it[1], "")
                                inner.description = list
                                QuestManager.saveFile(questID, innerID)
                                sender.editorInnerDesc(questID, innerID)
                            }
                        }
                    }
                }
            }
        }
        literal("del") {
            dynamic {
                dynamic {
                    dynamic {
                        execute<Player> { sender, content, argument ->
                            val questID = content.argument(-2)
                            val innerID = content.argument(-1)
                            val inner = QuestManager.getInnerQuestModule(questID, innerID) ?: return@execute
                            val index = argument.split(" ")[0].toInt()
                            val list = inner.description.toMutableList()
                            list.removeAt(index)
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