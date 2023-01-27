package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.event.DialogEvent
import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.api.manager.DataManager.doingTargets

object TDialog: TargetExtend<DialogEvent>() {

    override val name = "player dialog"

    init {
        event = DialogEvent::class
        tasker{
            player.doingTargets(name).forEach {
                val t = it.getTargetFrame()?: return@forEach
                if (dialogTrigger(dialogModule.dialogID, t)) {
                    Schedule.isNumber(player, "number", it)
                }
            }
            player
        }
    }

    /**
     * 对话触发
     */
    fun dialogTrigger(dialog: String, target: TargetFrame): Boolean {
        val condition = target.nodeMeta("dialog")
        return condition.contains(dialog)
    }

}