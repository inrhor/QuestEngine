package cn.inrhor.questengine.script.kether.expand.control

import cn.inrhor.questengine.api.packet.*
import cn.inrhor.questengine.common.database.data.DataPacketID
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.packet.PacketManager
import cn.inrhor.questengine.common.packet.PacketSpawner
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.common.util.Location
import taboolib.module.effect.Circle
import taboolib.module.effect.Polygon
import taboolib.module.kether.*
import taboolib.library.kether.*
import java.util.concurrent.CompletableFuture

enum class Type {
    SEND, REMOVE
}

/**
 * type send id where world loc...
 */
class KetherPacket {

    /*
     * packet send id where location [location]
     */
    class SendPacket(val packetID: String, val location: ParsedAction<*>): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(location).run<Location>().thenAccept {
                val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
                PacketManager.sendPacket(packetID, player.cast(), it)
            }
        }
    }

    /*
     * packet send id number [int] where location [location] [type] [value] [step]
     */
    class SendMathPacket(val packetID: String, val number: Int, val location: ParsedAction<*>,
                         val type: String, val value: Double, val step: Double): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(location).run<Location>().thenAccept {
                val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
                val dataPacketID = DataPacketID(player.cast(), packetID, number)
                val spawner = PacketSpawner(player.cast(), dataPacketID)
                val t = type.lowercase()
                if (t == "circle") {
                    Circle(it, value, step, spawner).show()
                }else if (t == "polygon") {
                    Polygon(value.toInt(), it, step, spawner).show()
                }
            }
        }
    }

    /**
     * packet remove viewer[all/player] id
     */
    class RemovePacket(val viewer: Boolean, val packetID: String): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            if (viewer) {
                val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
                removePacket(mutableSetOf(player.cast()), packetID)
                return CompletableFuture.completedFuture(null)
            }
            val viewers = mutableSetOf<Player>()
            viewers.addAll(Bukkit.getOnlinePlayers())
            removePacket(viewers, packetID)
            return CompletableFuture.completedFuture(null)
        }

        private fun removePacket(viewers: MutableSet<Player>, packetID: String) {
            val m = PacketManager.packetMap[packetID]?: return
            viewers.forEach {
                val pData = DataStorage.getPlayerData(it)
                if (pData.packetEntitys.containsKey(packetID)) {
                    pData.packetEntitys[packetID]!!.forEach { id ->
                        destroyEntity(it, id)
                    }
                }else {
                    destroyEntity(viewers, m.entityID)
                }
            }
        }
    }

    internal object Parser {
        @KetherParser(["packet"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            it.mark()
            when (it.expects("send", "remove")) {
                "send" -> {
                    val packetID = it.nextToken()
                    it.mark()
                    when (it.expects("where", "number")) {
                        "where" -> SendPacket(packetID, it.next(ArgTypes.ACTION))
                        "number" -> {
                            val number = it.nextInt()
                            it.mark()
                            it.expect("where")
                            val location = it.next(ArgTypes.ACTION)
                            val type = it.nextToken()
                            val value = it.nextDouble()
                            val step = it.nextDouble()
                            SendMathPacket(packetID, number, location, type, value, step)
                        }
                        else -> error("unknown -> packet send ...")
                    }
                }
                "remove" -> {
                    RemovePacket(
                        try {
                            it.mark()
                            it.expect("player")
                            true
                        } catch (ex: Exception) {
                            false
                        },
                        it.nextToken())
                }
                else -> error("worry send")
            }
        }
        /*fun parser() = scriptParser {
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
            if (action == Type.SEND) {
                val packetID = it.nextToken()
                it.mark()
                when (it.expects("where", "number")) {
                    "where" -> SendPacket(
                        packetID,
                        it.run {
                            it.mark()
                            it.expect("where")
                            it.next(ArgTypes.ACTION)
                        })
                    "number" -> SendMathPacket(
                        packetID,
                        it.nextInt(),
                        it.run {
                            it.mark()
                            it.expect("where")
                            it.next(ArgTypes.ACTION)
                        },
                        it.run {
                            it.mark()
                            it.expect("type")
                            it.nextToken()
                        },
                        it.nextDouble(), it.nextDouble()
                    )
                    else -> throw KetherError.CUSTOM.create("未知数据包语句")
                }
            }else {
                RemovePacket(
                    try {
                        it.mark()
                        it.expect("player")
                        true
                    } catch (ex: Exception) {
                        false
                    },
                    it.nextToken())
            }
        }*/
    }

}