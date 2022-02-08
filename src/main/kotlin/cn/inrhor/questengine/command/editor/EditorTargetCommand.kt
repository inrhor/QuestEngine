package cn.inrhor.questengine.command.editor

import cn.inrhor.questengine.common.edit.EditorList.editorTargetList
import cn.inrhor.questengine.common.edit.EditorTarget.editorTarget
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.subCommand

internal object EditorTargetCommand {

    @CommandBody
    val list = subCommand {
        dynamic {
            dynamic {
                dynamic {
                    execute<Player> { sender, content, argument ->
                        val questID = content.argument(-2)
                        val innerID = content.argument(-1)
                        val page = argument.split(" ")[0].toInt()
                        sender.editorTargetList(questID, innerID, page)
                    }
                }
            }
        }
    }

    @CommandBody
    val edit = subCommand {
        dynamic {
            dynamic {
                dynamic {
                    execute<Player> { sender, content, argument ->
                        val questID = content.argument(-2)
                        val innerID = content.argument(-1)
                        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return@execute
                        val page = argument.split(" ")[0].toInt()
                        sender.editorTarget(questID, innerID, inner.questTargetList.map { it.key }[page])
                    }
                }
            }
        }
    }

}