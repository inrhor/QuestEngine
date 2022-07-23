package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.common.database.data.doingTargets
import org.bukkit.entity.Entity
import org.bukkit.entity.FishHook
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerFishEvent

object TPlayerFish: TargetExtend<PlayerFishEvent>() {

    override val name = "player fish"

    init {
        event = PlayerFishEvent::class
        tasker{
            fish(player, name, caught, hook, state, expToDrop)
            player
        }
    }

    fun fish(player: Player, name: String, entity: Entity?, hook: FishHook, state: PlayerFishEvent.State, exp: Int) {
        player.doingTargets(name).forEach {
            val target = it.getTargetFrame()
            val e = target.nodeMeta("entitylist")?: listOf()
            val es = e.toList()
            val h = target.nodeMeta("hook")?: listOf()
            val hs = h.toList()
            val s = target.nodeMeta("state")?: listOf()
            val ss = s.toList()
            val p = target.nodeMeta("exp")?: listOf("0")
            val ps = p[0].toInt()
            val am = target.nodeMeta("amount")?: listOf("1")
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
            Schedule.run(player, name, it, amount)
        }
    }

}