package cn.inrhor.questengine.common.quest.target.node

import cn.inrhor.questengine.api.target.TargetNode
import org.bukkit.event.entity.EntityDamageEvent

class CauseNode(node: String = "cause") : TargetNode(node) {

    override fun contains(content: String): Boolean {
        EntityDamageEvent.DamageCause.values().forEach {
            if (it.toString() == content.uppercase()) return true
        }
        return false
    }

}