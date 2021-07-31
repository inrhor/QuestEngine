package cn.inrhor.questengine.command

import cn.inrhor.questengine.command.quest.*
import taboolib.common.platform.CommandBody

object QuestAdminCommand {

    @CommandBody
    val accept = QuestAccept

    @CommandBody
    val finish = QuestFinish

    @CommandBody
    val quit = QuestQuit

}