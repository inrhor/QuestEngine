package cn.inrhor.questengine.command.editor

import cn.inrhor.questengine.common.edit.EditorInner.editorInner
import cn.inrhor.questengine.common.edit.EditorList.editorInnerDesc
import cn.inrhor.questengine.common.edit.EditorList.editorNextInner
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.subCommand
import taboolib.module.nms.inputSign
import taboolib.platform.util.asLangText

object InnerEditCommand {

    @CommandBody
    val edit = subCommand {
        literal("name") {
            dynamic {
                dynamic {
                    execute<Player> { sender, content, argument ->
                        val questID = content.argument(-1)
                        val innerID = argument.split(" ")[0]
                        sender.inputSign(arrayOf(sender.asLangText("EDITOR-EDIT-INNER-NAME-INPUT"))) {
                            val innerModule = QuestManager.getInnerQuestModule(questID, innerID)?: return@inputSign
                            innerModule.name = it[1]
                            QuestManager.saveFile(questID, innerID)
                            sender.editorInner(questID, innerID)
                        }
                    }
                }
            }
        }
        literal("nextinner") {
            dynamic {
                dynamic {
                    dynamic {
                        execute<Player> { sender, content, argument ->
                            val questID = content.argument(-2)
                            val innerID = content.argument(-1)
                            val page = argument.split(" ")[0].toInt()
                            sender.editorNextInner(questID, innerID, page)
                        }
                    }
                    execute<Player> { sender, content, argument ->
                        val questID = content.argument(-1)
                        val innerID = argument.split(" ")[0]
                        sender.editorNextInner(questID, innerID)
                    }
                }
            }
        }
        literal("desc") {
            dynamic {
                dynamic {
                    execute<Player> { sender, content, argument ->
                        val questID = content.argument(-1)
                        val innerID = argument.split(" ")[0]
                        sender.editorInnerDesc(questID, innerID)
                    }
                }
            }
        }
        dynamic {
            dynamic {
                execute<Player> { sender, content, argument ->
                    val questID = content.argument(-1)
                    val innerID = argument.split(" ")[0]
                    sender.editorInner(questID, innerID)
                }
            }
        }
    }

    @CommandBody
    val change = InnerChangeCommand

}