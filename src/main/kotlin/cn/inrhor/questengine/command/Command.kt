package cn.inrhor.questengine.command

import taboolib.common.platform.CommandBody
import taboolib.common.platform.CommandHeader

@CommandHeader("questengine", ["qen"])
internal object Command {

    @CommandBody(permission = "QuestEngine.admin.quest")
    val quest = QuestCommand

    @CommandBody(permission = "QuestEngine.admin.quest")
    val innerQuest = QuestInnerCommand

    @CommandBody(permission = "QuestEngine.admin.reload")
    val reload = ReloadCommand.reload

    @CommandBody(permission = "QuestEngine.use.team")
    val team = TeamCommand

}