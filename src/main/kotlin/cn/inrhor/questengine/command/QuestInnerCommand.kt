package cn.inrhor.questengine.command

import cn.inrhor.questengine.command.innerQuest.*
import taboolib.common.platform.CommandBody

internal object QuestInnerCommand {

    @CommandBody(permission = "QuestEngine.admin.quest")
    val accept = QuestInnerAccept.accept

    @CommandBody(permission = "QuestEngine.admin.quest")
    val finish = QuestInnerFinish.finish

}