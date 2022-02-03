package cn.inrhor.questengine.command.editor

import cn.inrhor.questengine.common.edit.EditorInner.editorInner
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
                    execute<Player> { sender, content, argument ->
                        val questID = content.argument(-1)
                        val innerID = argument.split(" ")[0]
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

}