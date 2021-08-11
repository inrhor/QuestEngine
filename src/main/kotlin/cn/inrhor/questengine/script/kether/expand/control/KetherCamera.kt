package cn.inrhor.questengine.script.kether.expand.control

import cn.inrhor.questengine.api.camera
import cn.inrhor.questengine.common.packet.PacketManager
import taboolib.common.platform.ProxyPlayer
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class KetherCamera(val packetID: String): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
        val packet = PacketManager.packetMap[packetID]?: error("unknown packetID")
        val entityID = packet.entityID
        camera(player.cast(), entityID)
        return CompletableFuture.completedFuture(null)
    }

    internal object Parser {
        @KetherParser(["enterPacket"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            it.mark()
            it.expect("to")
            KetherCamera(it.nextToken())
        }
    }

}