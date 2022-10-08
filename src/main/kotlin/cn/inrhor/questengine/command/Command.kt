package cn.inrhor.questengine.command

import taboolib.common.platform.command.*


@CommandHeader("questengine", ["qen"], permission = "questengine.command")
internal object Command {

    @CommandBody(permission = "QuestEngine.admin.quest")
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

    @CommandBody(permission = "QuestEngine.admin.editor")
    val editor = EditorCommand.editor
}