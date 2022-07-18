package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.quest.target.TNpcLeft.match
import ink.ptms.adyeshach.api.event.AdyeshachEntityDamageEvent
import org.bukkit.Bukkit
import taboolib.common.platform.function.info

object TAdyLeft: TargetExtend<AdyeshachEntityDamageEvent>() {

    override val name = "left ady"

    init {
        if (Bukkit.getPluginManager().getPlugin("Adyeshach") != null) {
            event = AdyeshachEntityDamageEvent::class
            tasker {
                val player = player
                match(player, entity.id, name)
                player
            }
        }
    }

}