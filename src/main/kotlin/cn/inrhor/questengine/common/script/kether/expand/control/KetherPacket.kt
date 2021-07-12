package cn.inrhor.questengine.common.script.kether.expand.control

import io.izzel.taboolib.kotlin.kether.Kether.expects
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.kether.script
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class KetherPacket {

    /**
     * type send viewer id where world loc...
     */
    class SendPacket(val viewer: Boolean, val packetID: String): QuestAction<Void>() {
        override fun process(frame: QuestContext.Frame): CompletableFuture<Void> {
            if (viewer) {
                val player = frame.script().sender as? Player ?: error("unknown player")
                sendPacket(mutableSetOf(player), packetID)
                return CompletableFuture.completedFuture(null)
            }
            val viewers = mutableSetOf<Player>()
            viewers.addAll(Bukkit.getOnlinePlayers())
            sendPacket(viewers, packetID)
            return CompletableFuture.completedFuture(null)
        }

        /*
         * 需要在 packet 文件夹中构造数据包模块
         *
         * packetID格式 packet-id
         *
         * 将根据packetID检索id
         */
        private fun sendPacket(viewers: MutableSet<Player>, packetID: String) {
            // PacketModule 类中读取
        }
    }

    /**
     * packet remove viewer id
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
            // PacketModule 类中读取
        }
    }

    companion object {
        @KetherParser(["packet"])
        fun parser() = ScriptParser.parser {
            when (it.expects("send", "remove")) {
                "send" -> SendPacket(
                    try {
                        it.mark()
                        it.expect("player")
                        true
                    } catch (ex: Exception) {
                        false
                    },
                    it.nextToken())
                "remove" -> RemovePacket(
                    try {
                        it.mark()
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