package cn.inrhor.questengine.common.quest.target.extra

import cn.inrhor.questengine.api.event.ReplyEvent
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.TriggerUtils.triggerTarget
import cn.inrhor.questengine.common.quest.target.extra.TDialog.dialogTrigger
import cn.inrhor.questengine.common.quest.target.node.ObjectiveNode

object TDialogReply: TargetExtend<ReplyEvent>() {

    override val name = "player reply"

    init {
        event = ReplyEvent::class
        tasker{
            player.triggerTarget(name) { _, pass ->
                replyTrigger(dialogModule.dialogID, replyModule.replyID, pass)
            }
            player
        }
    }

    /**
     * 对话触发
     */
    fun replyTrigger(dialog: String, replyID: String, pass: ObjectiveNode): Boolean {
        if (!dialogTrigger(dialog, pass)) return false
        val reply = pass.reply
        return reply.isEmpty() || reply.any { it == replyID }
    }

}