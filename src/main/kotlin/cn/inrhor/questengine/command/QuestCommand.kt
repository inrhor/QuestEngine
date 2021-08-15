package cn.inrhor.questengine.command

import cn.inrhor.questengine.command.quest.*
import taboolib.common.platform.command.CommandBody

internal object QuestCommand {

    @CommandBody
    val info = QuestInfo.info

    @CommandBody
    val accept = QuestAccept.accept

    @CommandBody
    val finish = QuestFinish.finish

    @CommandBody
    val quit = QuestQuit.quit

}