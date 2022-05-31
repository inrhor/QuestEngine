package cn.inrhor.questengine.common.quest.target.node

import cn.inrhor.questengine.api.target.TargetNode
import org.bukkit.Material

class BlockNode(node: String = "block") : TargetNode(node) {

    override fun contains(content: String): Boolean {
        Material.values().forEach {
            if (it.toString() == content.uppercase()) return true
        }
        return false
    }

}