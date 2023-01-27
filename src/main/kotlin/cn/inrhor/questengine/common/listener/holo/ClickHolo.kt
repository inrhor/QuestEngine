package cn.inrhor.questengine.common.listener.holo

import cn.inrhor.questengine.api.event.HoloClickEvent
import cn.inrhor.questengine.api.event.ReplyEvent
import cn.inrhor.questengine.script.kether.runEvalSet
import taboolib.common.platform.event.SubscribeEvent

object ClickHolo {

    @SubscribeEvent
    fun click(ev: HoloClickEvent) {
        val holoBox = ev.holoHitBox
        val dialogHolo = holoBox.dialogHolo
        val replyModule = holoBox.replyModule
        val loc = dialogHolo.npcLoc
        val viewers = dialogHolo.viewers
        val dialog = dialogHolo.dialogModule
        val a = replyModule.script.replace("{{npcLocation}}",
                "where location "+loc.world?.name
                    +" "+loc.x+" "+loc.y+" "+loc.z)
        viewers.forEach { p ->
            ReplyEvent(p, dialog, replyModule).call()
        }
        runEvalSet(viewers, a) { s ->
            s.rootFrame().variables()["@QenDialogID"] = dialog.dialogID
        }
    }

}