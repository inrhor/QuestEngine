package cn.inrhor.questengine.common.listener.holo

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.dialog.DialogManager
import net.citizensnpcs.api.event.NPCLeftClickEvent
import net.citizensnpcs.api.event.NPCRightClickEvent
import taboolib.common.platform.SubscribeEvent

class ClickCitizens {

    @SubscribeEvent
    fun rightClickNPC(ev: NPCRightClickEvent) {
        val player = ev.clicker
        val npc = ev.npc.entity
        val npcLoc = npc.location
        val npcIDName = ev.npc.name
        DialogManager.sendDialogHolo(mutableSetOf(player), npcIDName, npcLoc)
    }

    @SubscribeEvent
    fun leftClickNPC(ev: NPCLeftClickEvent) {
        val player = ev.clicker
        val npc = ev.npc.entity
        val npcLoc = npc.location
        val npcIDName = ev.npc.name
        DialogManager.sendDialogHolo(mutableSetOf(player), npcIDName, npcLoc)
    }

    fun isCitizensNPC() = (QuestEngine.config.getString("dialog.npcPlugin") == "Citizens")
}