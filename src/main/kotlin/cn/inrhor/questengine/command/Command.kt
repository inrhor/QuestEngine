package cn.inrhor.questengine.command

import taboolib.common.platform.CommandBody
import taboolib.common.platform.CommandHeader

@CommandHeader("QuestEngine", ["que", "qen"])
object Command {

    @CommandBody(permission = "QuestEngine.admin.quest")
    val questAdmin = QuestAdminCommand

    val questUse = QuestUseCommand

    @CommandBody(permission = "QuestEngine.admin.reload")
    val reload = ReloadCommand

    @CommandBody(permission = "QuestEngine.use.team")
    val team = TeamCommand

}