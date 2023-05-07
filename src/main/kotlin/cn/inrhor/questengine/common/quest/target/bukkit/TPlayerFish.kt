package cn.inrhor.questengine.common.quest.target.bukkit

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.TriggerUtils.triggerTarget
import org.bukkit.event.player.PlayerFishEvent

object TPlayerFish: TargetExtend<PlayerFishEvent>() {

    override val name = "player fish"

    init {
        event = PlayerFishEvent::class
        tasker{
            player.triggerTarget(name) { _, pass ->
                val entityTypes = pass.entityTypes
                if (entityTypes.isNotEmpty() && !entityTypes.any { it == caught?.type?.name }) {
                    return@triggerTarget false
                }
                val h = pass.hook
                if (h.isNotEmpty() && !h.any { it == hook.hookedEntity?.type?.name }) {
                    return@triggerTarget false
                }
                val s = pass.state
                if (s.isNotEmpty() && !s.any { it == state.name }) {
                    return@triggerTarget false
                }
                pass.exp <= expToDrop
            }
        }
    }

}