package cn.inrhor.questengine.common.listener.holo

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.dialog.DialogManager
import ink.ptms.adyeshach.api.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent
import io.izzel.taboolib.module.inject.TListener
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener




@TListener(depend = ["Adyeshach"], condition = "isAdyesNPC")
class ClickAdyesNPC: Listener {

    @EventHandler
    fun rightClickNPC(ev: AdyeshachEntityInteractEvent) {
        if (!ev.isMainHand) return
        val player = ev.player
        val npc = ev.entity
        val npcLoc = npc.getLocation()
        val npcID = npc.id
        DialogManager().sendDialogHolo(mutableSetOf(player), npcID, npcLoc)

    }

    @EventHandler
    fun leftClickNPC(ev: AdyeshachEntityDamageEvent) {
        val player = ev.player
        val npc = ev.entity
        val npcLoc = npc.getLocation()
        val npcID = npc.id
        DialogManager().sendDialogHolo(mutableSetOf(player), npcID, npcLoc)
    }

    fun isAdyesNPC() = (QuestEngine.config.getString("dialog.npcPlugin") == "Adyeshach")
}