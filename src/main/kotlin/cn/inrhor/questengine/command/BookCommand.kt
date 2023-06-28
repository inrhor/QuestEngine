package cn.inrhor.questengine.command

import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.record.QuestRecord
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common5.Coerce

object BookCommand {

    val book = subCommand {
        literal("group") {
            literal("doing") {
                dynamic ("page") {
                    dynamic("start") {
                        execute<Player> { sender, context, _ ->
                            QuestRecord.sendGroup(sender,
                                Coerce.toInteger(context["page"]),
                                Coerce.toInteger(context["start"]),
                                StateType.DOING)
                        }
                    }
                }
            }
            literal("finish") {
                dynamic("page") {
                    dynamic("start") {
                        execute<Player> { sender, context, _ ->
                            QuestRecord.sendGroup(sender,
                                Coerce.toInteger(context["page"]),
                                Coerce.toInteger(context["start"]),
                                StateType.FINISH)
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