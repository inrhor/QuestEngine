package cn.inrhor.questengine.command.editor

import cn.inrhor.questengine.common.edit.EditorList.editorFinishReward
import cn.inrhor.questengine.common.edit.EditorList.editorRewardList
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.subCommand

internal object EditorRewardCommand {

    @CommandBody
    val list = subCommand {
        dynamic {
            dynamic {
                dynamic {
                    execute<Player> { sender, content, argument ->
                        val questID = content.argument(-2)
                        val innerID = content.argument(-1)
                        val page = argument.split(" ")[0].toInt()
                        sender.editorRewardList(questID, innerID, page)
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
                   dynamic {
                       execute<Player> { sender, content, argument ->
                           val questID = content.argument(-3)
                           val innerID = content.argument(-2)
                           val rewardID = content.argument(-1)
                           val page = argument.split(" ")[0].toInt()
                           sender.editorFinishReward(questID, innerID, rewardID, page)
                       }
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
                    dynamic {
                        execute<Player> { sender, content, argument ->
                            val questID = content.argument(-3)
                            val innerID = content.argument(-2)
                            val rewardID = content.argument(-1)
                            val index = argument.split(" ")[0].toInt()
                            val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return@execute
                            inner.reward.finish.removeAt(index)
                            QuestManager.saveFile(questID, innerID)
                            sender.editorFinishReward(questID, innerID, rewardID)
                        }
                    }
                }
            }
        }
    }

}