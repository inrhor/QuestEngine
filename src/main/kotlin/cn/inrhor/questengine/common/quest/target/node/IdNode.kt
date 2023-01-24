package cn.inrhor.questengine.common.quest.target.node

import cn.inrhor.questengine.api.target.TargetNode
import ink.ptms.adyeshach.api.AdyeshachAPI
import net.citizensnpcs.api.CitizensAPI
import org.bukkit.entity.Player
import taboolib.library.xseries.XMaterial

class IdNode(node: String = "id", vararg val more: String) : TargetNode(XMaterial.ENDER_EYE, node) {

    override fun contains(content: String, player: Player): Boolean {
        if (more.isEmpty()) return true
        return when (more[0]) {
            "Citizens" -> CitizensAPI.getNPCRegistry().getById(content.toInt()) != null
            "Adyeshach" -> {
                AdyeshachAPI.getEntityFromId(content) != null
            }
            else -> false
        }
    }

}