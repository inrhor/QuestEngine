package cn.inrhor.questengine.script.kether.expand.control

import cn.inrhor.questengine.common.nms.NMS
import cn.inrhor.questengine.common.packet.PacketManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.library.kether.*
import taboolib.module.kether.*
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

/**
 * type send id where world loc...
 */
class KetherPacket {

    enum class Type {
        SEND, REMOVE
    }

    /**
     * packet send id where world loc...
     */
    class SendPacket(val packetID: String, val location: ParsedAction<*>): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
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
    class RemovePacket(val viewer: Boolean, val packetID: String): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
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

    internal object Parser {
        @KetherParser(["packet"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            it.mark()
            val action = try {
                when (val type = it.nextToken()) {
                    "send" -> Type.SEND
                    "remove" -> Type.REMOVE
                    else -> throw KetherError.CUSTOM.create("未知数据包动作类型: $type")
                }
            } catch (ignored: Exception) {
                it.reset()
                Type.REMOVE
            }
            when (action) {
                Type.SEND -> SendPacket(
                    it.nextToken(),
                    it.run {
                        it.mark()
                        it.expect("where")
                        it.next(ArgTypes.ACTION)
                    })
                Type.REMOVE -> RemovePacket(
                    try {
                        it.mark()
                        it.expect("player")
                        true
                    } catch (ex: Exception) {
                        false
                    },
                    it.nextToken())
            }
        }
    }

}