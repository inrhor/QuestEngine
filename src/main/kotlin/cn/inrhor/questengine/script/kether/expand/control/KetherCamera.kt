package cn.inrhor.questengine.script.kether.expand.control

import cn.inrhor.questengine.api.packet.*
import cn.inrhor.questengine.common.packet.PacketManager
import cn.inrhor.questengine.utlis.location.LocationTool
import org.bukkit.Location
import taboolib.common.platform.ProxyPlayer
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class KetherCamera(val packetID: String, val location: ParsedAction<*>): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        return frame.newFrame(location).run<Location>().thenAccept {
            val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
            val packetList = PacketManager.getPacketDataList(player.cast(), packetID)
            packetList.forEach { p ->
                if (LocationTool.inLoc(p.location, it, 0.0)) {
                    camera(player.cast(), p.entityID)
                    return@thenAccept
                }
            }
        }
    }

    internal object Parser {
        @KetherParser(["enterPacket"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            it.mark()
            it.expect("to")
            val packetID = it.nextToken()
            it.mark()
            it.expect("where")
            KetherCamera(packetID, it.next(ArgTypes.ACTION))
        }
    }

}