package cn.inrhor.questengine.common.listener.holo

import cn.inrhor.questengine.common.dialog.DialogManager
import ink.ptms.adyeshach.api.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent
import taboolib.common.platform.event.*


class ClickAdyNPC{

    @SubscribeEvent(bind = "ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent")
    fun rightClickNPC(op: OptionalEvent) {
        val ev = op.cast(AdyeshachEntityInteractEvent::class.java)
        if (!ev.isMainHand) return
        val player = ev.player
        val npc = ev.entity
        val npcLoc = npc.location
        val npcID = npc.id
        DialogManager.sendDialogHolo(mutableSetOf(player), npcID, npcLoc)
    }

    @SubscribeEvent(bind = "ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent")
    fun leftClickNPC(op: OptionalEvent) {
        val ev = op.cast(AdyeshachEntityDamageEvent::class.java)
        val player = ev.player
        val npc = ev.entity
        val npcLoc = npc.location
        val npcID = npc.id
        DialogManager.sendDialogHolo(mutableSetOf(player), npcID, npcLoc)
    }
}