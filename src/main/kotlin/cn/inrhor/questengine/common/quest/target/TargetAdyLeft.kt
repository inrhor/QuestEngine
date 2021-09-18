package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.quest.manager.TargetManager
import ink.ptms.adyeshach.api.event.AdyeshachEntityDamageEvent
import org.bukkit.Bukkit

object TargetAdyLeft: TargetExtend<AdyeshachEntityDamageEvent>() {

    override val name = "left ady"

    init {
        if (Bukkit.getPluginManager().getPlugin("Adyeshach") != null) {
            event = AdyeshachEntityDamageEvent::class
            tasker {
                TargetNpcLeft.match(player, entity.id)
                player
            }
            TargetManager.register(name, "id", mutableListOf("id"))
            TargetManager.register(name, "need", mutableListOf("need"))
        }
    }

}