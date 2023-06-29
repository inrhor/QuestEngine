package cn.inrhor.questengine.command

import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.record.QuestRecord
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.component.CommandComponent
import taboolib.common.platform.command.subCommand
import taboolib.common5.Coerce

object BookCommand {

    val book = subCommand {
        dynamic("type") {
            suggestion<Player> { _, _ ->
                listOf("group", "quest", "target")
            }
            dynamic("state") {
                suggestion<Player> { _, _ ->
                    listOf("DOING", "FINISH", "FAILURE")
                }
                dynamic ("page") {
                    dynamic("start") {
                        execute<Player> { sender, context, argument ->
                            when (context["type"]) {
                                "quest" -> {
                                    val sp = argument.split(" ")
                                    QuestRecord.sendQuest(sender,
                                        Coerce.toInteger(context["start"]),
                                        Coerce.toInteger(context["page"]),
                                        StateType.valueOf(context["state"]),
                                        sp[1])
                                }
                                "target" -> {

                                }
                                else -> {
                                    QuestRecord.sendGroup(sender,
                                        Coerce.toInteger(context["start"]),
                                        Coerce.toInteger(context["page"]),
                                        StateType.valueOf(context["state"]))
                                }
                            }
                        }
                    }
                }
            }
        }
        execute<Player> { sender, _, _ ->
            QuestRecord.sendHome(sender)
        }
    }

}