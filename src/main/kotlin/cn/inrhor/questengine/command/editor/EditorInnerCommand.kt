package cn.inrhor.questengine.command.editor

import cn.inrhor.questengine.common.edit.EditorList.editorListInner
import cn.inrhor.questengine.common.edit.EditorList.editorListQuest
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.subCommand

internal object EditorInnerCommand {

    @CommandBody
    val edit = InnerEditCommand

    @CommandBody
    val list = subCommand {
        dynamic {
            dynamic {
                execute<Player> { sender, content, argument ->
                    val args = argument.split(" ")
                    sender.editorListInner(content.argument(-1), args[0].toInt())
                }
            }
            execute<Player> { sender, _, argument ->
                val args = argument.split(" ")
                sender.editorListInner(args[0])
            }
        }
    }

    @CommandBody
    val del = subCommand {
        dynamic {
            suggestion<Player> { _, _ ->
                QuestManager.questMap.keys.map { it }
            }
            dynamic {
                suggestion<Player> { _, content ->
                    QuestManager.questMap[content.argument(-1)]?.innerQuestList?.map { it.id }
                }
                execute<Player> { sender, content, argument ->
                    val args = argument.split(" ")
                    val questID = content.argument(-1)
                    QuestManager.delInner(questID, args[0])
                    sender.editorListInner(questID)
                }
            }
        }
    }

}