package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.quest.manager.TargetManager
import ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent
import org.bukkit.Bukkit

object TargetAdyRightItem: TargetExtend<AdyeshachEntityInteractEvent>() {

    override val name = "right ady"

    init {
        if (Bukkit.getPluginManager().getPlugin("Adyeshach") != null) {
            event = AdyeshachEntityInteractEvent::class
            tasker {
                TargetNpcLeft.match(player, entity.id)
                player
            }
            TargetManager.register(name, "id", mutableListOf("id"))
            TargetManager.register(name, "need", mutableListOf("need"))
        }
    }


}