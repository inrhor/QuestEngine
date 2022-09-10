package cn.inrhor.questengine.common.quest.target.node

import cn.inrhor.questengine.api.target.TargetNode
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.um.Mythic
import net.citizensnpcs.api.CitizensAPI
import org.bukkit.entity.Player

class IdNode(node: String = "id", vararg val more: String) : TargetNode(node) {

    override fun contains(content: String, player: Player): Boolean {
        if (more.isEmpty()) return true
        return when (more[0]) {
            "MythicMobs" -> {
                Mythic.API.getMobIDList().contains(content)
            }
            "Citizens" -> CitizensAPI.getNPCRegistry().getById(content.toInt()) != null
            "Adyeshach" -> {
                AdyeshachAPI.getEntityFromId(content) != null
            }
            else -> false
        }
    }

}