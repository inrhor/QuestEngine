package cn.inrhor.questengine.common.listener.holo

import cn.inrhor.questengine.api.event.HoloClickEvent
import cn.inrhor.questengine.script.kether.eval
import taboolib.common.platform.event.SubscribeEvent

object ClickHolo {

    @SubscribeEvent
    fun click(ev: HoloClickEvent) {
        val holoBox = ev.holoHitBox
        val replyModule = holoBox.replyModule
        holoBox.viewers.forEach {
            for (script in replyModule.script) {
                if (script.lowercase().startsWith("npcdialog send")) {
                    val loc = holoBox.npcLoc
                    eval(it, script.replace("npcdialog", "dialog", true)
                            +" where location *"+loc.world?.name
                            +" *"+loc.x+" *"+loc.y+" *"+loc.z)
                }else {
                    eval(it, script)
                }
            }
        }
    }

}