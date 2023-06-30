package cn.inrhor.questengine.command.main

import cn.inrhor.questengine.command.quest.*
import taboolib.common.platform.command.CommandBody

internal object QuestCommand {

    @CommandBody
    val accept = QuestAccept.accept

    @CommandBody
    val finish = QuestFinish.finish

    @CommandBody
    val track = QuestTrack.track

    @CommandBody
    val quit = QuestQuit.quit

}