package cn.inrhor.questengine.common.quest.target.adyeshach

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.quest.target.citizens.TNpcLeft.match
import ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent
import org.bukkit.Bukkit

object TAdyRight: TargetExtend<AdyeshachEntityInteractEvent>() {

    override val name = "right ady"

    init {
        if (Bukkit.getPluginManager().getPlugin("Adyeshach") != null) {
            event = AdyeshachEntityInteractEvent::class
            tasker {
                val player = player
                match(player, entity.id, name)
            }
        }
    }

}