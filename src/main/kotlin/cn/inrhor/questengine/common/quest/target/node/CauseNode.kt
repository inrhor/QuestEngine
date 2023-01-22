package cn.inrhor.questengine.common.quest.target.node

import cn.inrhor.questengine.api.target.TargetNode
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import taboolib.library.xseries.XMaterial

class CauseNode(node: String = "cause") : TargetNode(XMaterial.CLAY_BALL, node) {

    override fun contains(content: String, player: Player): Boolean {
        return try {
            EntityDamageEvent.DamageCause.valueOf(content.uppercase())
            true
        }catch (ex: Exception) {
            false
        }
    }

}