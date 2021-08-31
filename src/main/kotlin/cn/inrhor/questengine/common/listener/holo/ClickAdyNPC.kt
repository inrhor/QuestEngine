package cn.inrhor.questengine.common.listener.holo

import cn.inrhor.questengine.common.dialog.DialogManager
import ink.ptms.adyeshach.api.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent
import ink.ptms.adyeshach.common.entity.EntityInstance
import org.bukkit.entity.Player
import taboolib.common.platform.event.*


class ClickAdyNPC {

    @SubscribeEvent(bind = "ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent")
    fun rightClickNPC(op: OptionalEvent) {
        val ev = op.get<AdyeshachEntityInteractEvent>()
        if (!ev.isMainHand) return
        sendDialog(ev.player, ev.entity)
    }

    @SubscribeEvent(bind = "ink.ptms.adyeshach.api.event.AdyeshachEntityDamageEvent")
    fun leftClickNPC(op: OptionalEvent) {
        val ev = op.get<AdyeshachEntityDamageEvent>()
        sendDialog(ev.player, ev.entity)
    }

    private fun sendDialog(player: Player, npc: EntityInstance) {
        val npcLoc = npc.getLocation()
        val npcID = npc.id
        DialogManager.sendDialogHolo(mutableSetOf(player), npcID, npcLoc)
    }
}