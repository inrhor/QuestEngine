package cn.inrhor.questengine.common.quest.target.node

import cn.inrhor.questengine.api.target.TargetNode
import net.citizensnpcs.api.CitizensAPI
import org.bukkit.Bukkit

class IdNode(node: String = "id") : TargetNode(node) {

    override fun contains(content: String): Boolean {
        if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
            return CitizensAPI.getNPCRegistry().getById(content.toInt()) != null
        }
        return false
    }

}