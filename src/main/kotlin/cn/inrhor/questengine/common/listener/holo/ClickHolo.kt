package cn.inrhor.questengine.common.listener.holo

import cn.inrhor.questengine.api.event.HoloClickEvent
import cn.inrhor.questengine.script.kether.runEvalSet
import taboolib.common.platform.event.SubscribeEvent

object ClickHolo {

    @SubscribeEvent
    fun click(ev: HoloClickEvent) {
        val holoBox = ev.holoHitBox
        val replyModule = holoBox.replyModule
        val loc = holoBox.dialogHolo.npcLoc
        val a = replyModule.script.replace("{{npcLocation}}",
                "where location "+loc.world?.name
                    +" "+loc.x+" "+loc.y+" "+loc.z)
        runEvalSet(holoBox.dialogHolo.viewers, a) { s ->
            s.rootFrame().variables()["@QenDialogID"] = holoBox.dialogHolo.dialogModule.dialogID
        }
    }

}