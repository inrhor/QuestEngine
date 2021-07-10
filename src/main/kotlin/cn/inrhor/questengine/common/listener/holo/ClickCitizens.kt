package cn.inrhor.questengine.common.listener.holo

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.dialog.DialogManager
import io.izzel.taboolib.module.inject.TListener
import net.citizensnpcs.api.event.NPCLeftClickEvent
import net.citizensnpcs.api.event.NPCRightClickEvent
import org.bukkit.event.EventHandler

@TListener(depend = ["Citizens"], condition = "isCitizensNPC")
class ClickCitizens {
    @EventHandler
    fun rightClickNPC(ev: NPCRightClickEvent) {
        val player = ev.clicker
        val npc = ev.npc.entity
        val npcLoc = npc.location
        val npcIDName = ev.npc.name
        DialogManager.sendDialogHolo(mutableSetOf(player), npcIDName, npcLoc)
    }

    @EventHandler
    fun leftClickNPC(ev: NPCLeftClickEvent) {
        val player = ev.clicker
        val npc = ev.npc.entity
        val npcLoc = npc.location
        val npcIDName = ev.npc.name
        DialogManager.sendDialogHolo(mutableSetOf(player), npcIDName, npcLoc)
    }

    fun isCitizensNPC() = (QuestEngine.config.getString("dialog.npcPlugin") == "Citizens")
}