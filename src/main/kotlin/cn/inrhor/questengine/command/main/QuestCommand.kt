package cn.inrhor.questengine.command.main

import cn.inrhor.questengine.command.quest.*
import taboolib.common.platform.command.CommandBody

internal object QuestCommand {

    @CommandBody(permission = "QuestEngine.admin.quest")
    val accept = QuestAccept.accept

    @CommandBody(permission = "QuestEngine.admin.quest")
    val finish = QuestFinish.finish

    @CommandBody(permission = "QuestEngine.use.quest.track")
    val track = QuestTrack.track

    @CommandBody(permission = "QuestEngine.admin.quest.quit")
    val quit = QuestQuit.quit

}