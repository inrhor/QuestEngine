package cn.inrhor.questengine.common.quest.target.extra

import cn.inrhor.questengine.api.event.DialogEvent
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.TriggerUtils.triggerTarget
import cn.inrhor.questengine.common.quest.target.node.ObjectiveNode

object TDialog: TargetExtend<DialogEvent>() {

    override val name = "player dialog"

    init {
        event = DialogEvent::class
        tasker{
            player.triggerTarget(name) { _, pass ->
                dialogTrigger(dialogModule.dialogID, pass)
            }
            player
        }
    }

    /**
     * 对话触发
     */
    fun dialogTrigger(dialog: String, pass: ObjectiveNode): Boolean {
        val d = pass.dialog
        return d.isEmpty() || d.any { it == dialog }
    }

}