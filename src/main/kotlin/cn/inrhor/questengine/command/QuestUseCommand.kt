package cn.inrhor.questengine.command

import cn.inrhor.questengine.command.quest.*
import taboolib.common.platform.CommandBody

object QuestUseCommand {

    @CommandBody(permission = "QuestEngine.use.questInfo")
    val info = QuestInfo

}