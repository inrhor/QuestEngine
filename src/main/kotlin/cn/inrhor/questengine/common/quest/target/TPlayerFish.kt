package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.api.manager.DataManager.doingTargets
import cn.inrhor.questengine.api.target.util.TriggerUtils.triggerTarget
import org.bukkit.entity.Entity
import org.bukkit.entity.FishHook
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerFishEvent

object TPlayerFish: TargetExtend<PlayerFishEvent>() {

    override val name = "player fish"

    init {
        event = PlayerFishEvent::class
        tasker{
//            fish(player, name, caught, hook, state, expToDrop)
//            player
            player.triggerTarget(name) { _, pass ->
                val entityTypes = pass.entityTypes
                if (entityTypes.isNotEmpty() && !entityTypes.any { it == caught?.type?.name }) {
                    return@triggerTarget false
                }

            }
        }
    }

    fun fish(player: Player, name: String, entity: Entity?, hook: FishHook, state: PlayerFishEvent.State, exp: Int) {
        player.doingTargets(name).forEach {
            val target = it.getTargetFrame()?: return@forEach
            val e = target.nodeMeta("entitylist")
            val es = e.toList()
            val h = target.nodeMeta("hook")
            val hs = h.toList()
            val s = target.nodeMeta("state")
            val ss = s.toList()
            val p = target.nodeMeta("exp", "0")
            val ps = p[0].toInt()
            val am = target.nodeMeta("amount", "1")
            val amount = am[0].toInt()
            if (es.isNotEmpty() && entity != null) {
                if (!es.contains(entity.type.toString())) return
            }
            if (hs.isNotEmpty()) {
                if (!hs.contains(hook.hookedEntity?.type.toString())) return
            }
            if (ss.isNotEmpty()) {
                if (!ss.contains(state.toString())) return
            }
            if (ps > 0 && exp < ps) return
            Schedule.run(player, it, amount)
        }
    }

}