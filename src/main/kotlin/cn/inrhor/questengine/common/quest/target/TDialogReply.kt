package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.event.DialogEvent
import cn.inrhor.questengine.api.event.ReplyEvent
import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.api.manager.DataManager.doingTargets
import cn.inrhor.questengine.common.quest.target.TDialog.dialogTrigger

object TDialogReply: TargetExtend<ReplyEvent>() {

    override val name = "player reply"

    init {
        event = ReplyEvent::class
        tasker{
            player.doingTargets(name).forEach {
                val t = it.getTargetFrame()?: return@forEach
                if (replyTrigger(dialogModule.dialogID, replyModule.replyID, t)) {
                    Schedule.isNumber(player, "number", it)
                }
            }
            player
        }
    }

    /**
     * 对话触发
     */
    fun replyTrigger(dialog: String, replyID: String, target: TargetFrame): Boolean {
        if (!dialogTrigger(dialog, target)) return false
        val condition = target.nodeMeta("reply")
        return condition.contains(replyID)
    }

}