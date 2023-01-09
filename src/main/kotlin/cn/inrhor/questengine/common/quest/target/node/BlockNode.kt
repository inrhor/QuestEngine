package cn.inrhor.questengine.common.quest.target.node

import cn.inrhor.questengine.api.target.TargetNode
import org.bukkit.Material
import org.bukkit.entity.Player

class BlockNode(node: String = "block") : TargetNode(node) {

    override fun contains(content: String, player: Player): Boolean {
        return try {
            Material.valueOf(content.uppercase())
            true
        }catch (ex: Exception) {
            false
        }
    }

}