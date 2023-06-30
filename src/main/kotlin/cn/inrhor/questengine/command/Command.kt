package cn.inrhor.questengine.command

import cn.inrhor.questengine.command.main.*
import cn.inrhor.questengine.command.main.DialogCommand
import cn.inrhor.questengine.command.main.TeamCommand
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper


@CommandHeader("questengine", ["qen"], permission = "questengine.command")
internal object Command {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val quest = QuestCommand

    @CommandBody(permission = "QuestEngine.admin.eval")
    val eval = EvalCommand.eval

    @CommandBody(permission = "QuestEngine.admin.dialog")
    val dialog = DialogCommand.dialog

    @CommandBody(permission = "QuestEngine.admin.item")
    val item = ItemCommand.item

    @CommandBody(permission = "QuestEngine.admin.reload")
    val reload = ReloadCommand.reload

    @CommandBody(permission = "QuestEngine.use.team")
    val team = TeamCommand

    @CommandBody(permission = "QuestEngine.admin.tags")
    val tags = TagsCommand

    @CommandBody(permission = "QuestEngine.use.book")
    val book = BookCommand.book

    @CommandBody(permission = "QuestEngine.admin.migrate")
    val migrate = MigrateCommand.migrate
}