package cn.inrhor.questengine.script.kether.expand.control

import cn.inrhor.questengine.common.nms.NMS
import cn.inrhor.questengine.common.packet.PacketManager
import io.izzel.taboolib.kotlin.kether.Kether.expects
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.common.api.ParsedAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.script
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class KetherPacket {

    /**
     * type send id where world loc...
     */
    class SendPacket(val packetID: String, val location: ParsedAction<*>): QuestAction<Void>() {
        override fun process(frame: QuestContext.Frame): CompletableFuture<Void> {
            return frame.newFrame(location).run<Location>().thenAccept {
                val player = frame.script().sender as? Player ?: error("unknown player")
                sendPacket(packetID, player, it)
            }
        }

        /*
         * 需要在 packet 文件夹中构造数据包模块
         *
         * packetID格式 packet-id-type[entity/item]
         *
         * 将根据packetID检索id
         */
        private fun sendPacket(packetID: String, sender: Player, location: Location) {
            PacketManager.sendThisPacket(packetID, sender, location)
        }
    }

    /**
     * packet remove viewer[all/player] id
     */
    class RemovePacket(val viewer: Boolean, val packetID: String): QuestAction<Void>() {
        override fun process(frame: QuestContext.Frame): CompletableFuture<Void> {
            if (viewer) {
                val player = frame.script().sender as? Player ?: error("unknown player")
                removePacket(mutableSetOf(player), packetID)
                return CompletableFuture.completedFuture(null)
            }
            val viewers = mutableSetOf<Player>()
            viewers.addAll(Bukkit.getOnlinePlayers())
            removePacket(viewers, packetID)
            return CompletableFuture.completedFuture(null)
        }

        private fun removePacket(viewers: MutableSet<Player>, packetID: String) {
            val m = PacketManager.packetMap[packetID]?: return
            val id = m.entityID
            getPackets().destroyEntity(viewers, id)
        }

        private fun getPackets(): NMS {
            return NMS.INSTANCE
        }
    }

    companion object {
        @KetherParser(["packet"])
        fun parser() = ScriptParser.parser {
            when (it.expects("send", "remove")) {
                "send" -> SendPacket(
                    it.nextToken(),
                    it.run {
                        it.expect("where")
                        it.next(ArgTypes.ACTION)
                    })
                "remove" -> RemovePacket(
                    try {
                        it.expect("player")
                        true
                    } catch (ex: Exception) {
                        false
                    },
                    it.nextToken())
                else -> error("unknown type")
            }
        }
    }

}