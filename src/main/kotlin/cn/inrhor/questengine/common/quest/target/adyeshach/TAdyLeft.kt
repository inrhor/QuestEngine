package cn.inrhor.questengine.common.quest.target.adyeshach

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.quest.target.citizens.TNpcLeft.match
import ink.ptms.adyeshach.api.event.AdyeshachEntityDamageEvent
import org.bukkit.Bukkit

object TAdyLeft: TargetExtend<AdyeshachEntityDamageEvent>() {

    override val name = "left ady"

    init {
        if (Bukkit.getPluginManager().getPlugin("Adyeshach") != null) {
            event = AdyeshachEntityDamageEvent::class
            tasker {
                val player = player
                match(player, entity.id, name)
            }
        }
    }

}