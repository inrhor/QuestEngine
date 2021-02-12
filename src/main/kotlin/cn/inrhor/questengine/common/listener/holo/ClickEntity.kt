package cn.inrhor.questengine.common.listener.holo

import cn.inrhor.questengine.api.dialog.Dialog
import cn.inrhor.questengine.common.hologram.IHolo
import cn.inrhor.questengine.utlis.public.MsgUtil
import ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent
import io.izzel.taboolib.module.inject.TListener
import net.citizensnpcs.api.event.NPCLeftClickEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

@TListener
class ClickEntity: Listener {

    /*@EventHandler
    fun clickAdyesNPC(ev: AdyeshachEntityInteractEvent) {
        MsgUtil.send("eee")
        val player = ev.player
        val loc = ev.entity.getLocation()
        val dialogFile = Dialog().getDialog("dialog_1")!!
        val textList = dialogFile.ownTextContent
        val holo = IHolo(
            dialogFile.dialogID!!, loc, loc,
            mutableSetOf(player),
            textList!!,
            dialogFile.getOwnDialogItemList())
        holo.init()
    }

    @EventHandler
    fun clickCitizensNPC(ev: NPCLeftClickEvent) {

    }*/

}