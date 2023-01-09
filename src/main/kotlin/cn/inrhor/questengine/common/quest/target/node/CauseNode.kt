package cn.inrhor.questengine.common.quest.target.node

import cn.inrhor.questengine.api.target.TargetNode
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

class CauseNode(node: String = "cause") : TargetNode(node) {

    override fun contains(content: String, player: Player): Boolean {
        return try {
            EntityDamageEvent.DamageCause.valueOf(content.uppercase())
            true
        }catch (ex: Exception) {
            false
        }
    }

}