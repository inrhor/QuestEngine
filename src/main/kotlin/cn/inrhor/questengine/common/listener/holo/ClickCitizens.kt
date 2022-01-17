package cn.inrhor.questengine.common.listener.holo

import cn.inrhor.questengine.common.dialog.DialogManager
import net.citizensnpcs.api.event.NPCLeftClickEvent
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.npc.NPC
import org.bukkit.entity.Player
import taboolib.common.platform.event.*

object ClickCitizens {

    @SubscribeEvent(bind = "net.citizensnpcs.api.event.NPCRightClickEvent")
    fun rightClickNPC(op: OptionalEvent) {
        val ev = op.get<NPCRightClickEvent>()
        val player = ev.clicker
        val npc = ev.npc
        npc(player, npc)
    }

    @SubscribeEvent(bind = "net.citizensnpcs.api.event.NPCLeftClickEvent")
    fun leftClickNPC(op: OptionalEvent) {
        val ev = op.get<NPCLeftClickEvent>()
        val player = ev.clicker
        val npc = ev.npc
        npc(player, npc)
    }

    private fun npc(player: Player, npc: NPC) {
        val npcLoc = npc.entity.location
        val npcID= npc.id.toString()
        DialogManager.sendDialogHolo(mutableSetOf(player), npcID, npcLoc)
    }
}