package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.api.manager.DataManager.doingTargets
import cn.inrhor.questengine.api.target.util.TriggerUtils.triggerTarget
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object TPlayerCommand: TargetExtend<PlayerCommandPreprocessEvent>() {

    override val name = "player send command"

    init {
        event = PlayerCommandPreprocessEvent::class
        tasker{
            player.triggerTarget(name) { _, pass ->
                val content = pass.content
                content.isEmpty() || content.any { it == message }
            }
        }
    }

}