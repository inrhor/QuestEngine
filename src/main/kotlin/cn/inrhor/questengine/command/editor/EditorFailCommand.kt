package cn.inrhor.questengine.command.editor

import cn.inrhor.questengine.common.edit.EditorList.editorFailReward
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.subCommand

internal object EditorFailCommand {

    @CommandBody
    val list = subCommand {
        dynamic {
            dynamic {
                dynamic {
                    execute<Player> { sender, content, argument ->
                        val questID = content.argument(-2)
                        val innerID = content.argument(-1)
                        val page = argument.split(" ")[0].toInt()
                        sender.editorFailReward(questID, innerID, page)
                    }
                }
            }
        }
    }

    @CommandBody
    val del = subCommand {
        dynamic {
            dynamic {
                dynamic {
                    execute<Player> { sender, content, argument ->
                        val questID = content.argument(-2)
                        val innerID = content.argument(-1)
                        val index = argument.split(" ")[0].toInt()
                        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return@execute
                        val list = inner.reward.fail.toMutableList()
                        list.removeAt(index)
                        inner.reward.fail = list
                        QuestManager.saveFile(questID, innerID)
                        sender.editorFailReward(questID, innerID)
                    }
                }
            }
        }
    }

}