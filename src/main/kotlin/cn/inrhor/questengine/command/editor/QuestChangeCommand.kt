package cn.inrhor.questengine.command.editor

import cn.inrhor.questengine.common.edit.EditorList.editorAcceptCondition
import cn.inrhor.questengine.common.edit.EditorList.editorStartInner
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.subCommand

object QuestChangeCommand {

    @CommandBody
    val start = subCommand {
        dynamic {
            dynamic {
                suggestion<Player> { _, context ->
                    QuestManager.getQuestModule(context.argument(-1))?.innerQuestList?.map { it.id }
                }
                execute<Player> { sender, content, argument ->
                    val questID = content.argument(-1)
                    val questModule = QuestManager.getQuestModule(questID)?: return@execute
                    val innerID = argument.split(" ")[0]
                    questModule.startInnerQuestID = innerID
                    sender.editorStartInner(questID)
                    QuestManager.saveFile(questID, innerID)
                }
            }
        }
    }


    @CommandBody
    val acceptcondition = subCommand {
        dynamic {
            dynamic {
                execute<Player> { sender, content, argument ->
                    val questID = content.argument(-1)
                    val questModule = QuestManager.getQuestModule(questID) ?: return@execute
                    val index = argument.split(" ")[0].toInt()
                    questModule.accept.condition.removeAt(index)
                    QuestManager.saveFile(questID)
                    sender.editorAcceptCondition(questID)
                }
            }
        }
    }
}