package cn.inrhor.questengine.common.quest.target.bukkit

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.TriggerUtils.triggerTarget
import cn.inrhor.questengine.script.kether.runEval
import org.bukkit.event.player.PlayerSwapHandItemsEvent

object TPlayerSwapHandItems: TargetExtend<PlayerSwapHandItemsEvent>() {

    override val name = "player swap hand items"

    init {
        event = PlayerSwapHandItemsEvent::class
        tasker {
            player.triggerTarget(name) { _, pass ->
                val need = pass.need
                need.isEmpty() || runEval(player, need)
            }
        }
    }

}