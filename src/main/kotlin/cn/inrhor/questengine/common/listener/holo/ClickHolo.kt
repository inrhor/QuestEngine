package cn.inrhor.questengine.common.listener.holo

import cn.inrhor.questengine.api.event.HoloClickEvent
import cn.inrhor.questengine.script.kether.runEval
import taboolib.common.platform.event.SubscribeEvent

object ClickHolo {

    @SubscribeEvent
    fun click(ev: HoloClickEvent) {
        val holoBox = ev.holoHitBox
        val replyModule = holoBox.replyModule
        holoBox.dialogHolo.viewers.forEach {
            for (script in replyModule.script) {
                if (script.lowercase().startsWith("npcdialog send")) {
                    val loc = holoBox.dialogHolo.npcLoc
                    runEval(it, script.replace("npcdialog", "dialog", true)
                            +" where location *"+loc.world?.name
                            +" *"+loc.x+" *"+loc.y+" *"+loc.z)
                }else {
                    runEval(it, script)
                }
            }
        }
    }

}