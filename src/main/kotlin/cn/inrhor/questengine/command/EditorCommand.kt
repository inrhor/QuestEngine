package cn.inrhor.questengine.command

import cn.inrhor.questengine.command.editor.EditorQuestCommand
import taboolib.common.platform.command.CommandBody

internal object EditorCommand {

    @CommandBody
    val quest = EditorQuestCommand

}