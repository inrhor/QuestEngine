package cn.inrhor.questengine.script.kether.expand.control

import cn.inrhor.questengine.api.packet.*
import cn.inrhor.questengine.common.packet.DataPacketID
import cn.inrhor.questengine.common.packet.PacketManager
import cn.inrhor.questengine.common.packet.spawner.PacketEntitySpawner
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import org.bukkit.Location
import taboolib.module.effect.Circle
import taboolib.module.effect.Polygon
import taboolib.module.kether.*
import taboolib.library.kether.*
import taboolib.platform.util.toProxyLocation
import java.util.concurrent.CompletableFuture


class KetherPacket {

    /*
     * packet send packet where location [location]
     */
    class SendPacket(val packetID: String, val location: ParsedAction<*>): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(location).run<Location>().thenAccept {
                val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
                val packetModule = PacketManager.packetMap[packetID]?: return@thenAccept
                val dataPacketID = DataPacketID(player.cast(), packetModule, 1, it)
                PacketManager.sendThisPacket(packetModule, player.cast(), it, dataPacketID)
            }
        }
    }

    /*
     * packet send [packetID] number [int] where location [location] type [type] [value] [step]
     */
    class SendMathPacket(val packetID: String, val number: Int, val location: ParsedAction<*>,
                         val type: String, val value: Double, val step: Double): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(location).run<Location>().thenAccept {
                val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
                val packetModule = PacketManager.packetMap[packetID]?: return@thenAccept
                val dataPacketID = DataPacketID(player.cast(), packetModule, number, it)
                val spawner = PacketEntitySpawner(player.cast(), dataPacketID)
                val t = type.lowercase()
                val loc = it.toProxyLocation()
                if (t == "circle") {
                    Circle(loc, value, step, spawner).show()
                }else if (t == "polygon") {
                    Polygon(value.toInt(), loc, step, spawner).show()
                }
            }
        }
    }

    /**
     * packet remove packetID viewer[all/player] where location <range double>
     */
    class RemoveRangePacket(val viewer: Boolean, val packetID: String, val location: ParsedAction<*>, val range: Double): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(location).run<Location>().thenAccept {
                if (viewer) {
                    val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
                    removeRangePacket(mutableSetOf(player.cast()), packetID, it, range)
                    return@thenAccept
                }
                val viewers = mutableSetOf<Player>()
                viewers.addAll(Bukkit.getOnlinePlayers())
                removeRangePacket(viewers, packetID, it, range)
            }
        }

        private fun removeRangePacket(viewers: MutableSet<Player>, packetID: String, location: Location, range: Double) {
            viewers.forEach {
                PacketManager.removePacketEntity(it, packetID, location, range)
            }
        }
    }

    /**
     * packet remove packetID viewer[all/player] where location type [type] [value] [step]
     */
    /*class RemoveTypePacket(val viewer: Boolean, val packetID: String, val location: ParsedAction<*>,
                           val type: String, val value: Double, val step: Double): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(location).run<Location>().thenAccept {
                if (viewer) {
                    val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
                    removeTypePacket(mutableSetOf(player.cast()), packetID, it, type, value, step)
                    return@thenAccept
                }
                val viewers = mutableSetOf<Player>()
                viewers.addAll(Bukkit.getOnlinePlayers())
                removeTypePacket(viewers, packetID, it, type, value, step)
            }
        }

        private fun removeTypePacket(viewers: MutableSet<Player>, packetID: String, location: Location,
                                     type: String, value: Double, step: Double) {
            viewers.forEach {
                val pData = DataStorage.getPlayerData(it)
                val spawner = PacketRemoveSpawner(it, pData)
                val t = type.lowercase()
                if (t == "circle") {
                    Circle(location, value, step, spawner).show()
                }else if (t == "polygon") {
                    Polygon(value.toInt(), location, step, spawner).show()
                }
            }
        }
    }*/

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
                            it.mark()
                            it.expect("type")
                            val type = it.nextToken()
                            val value = it.nextDouble()
                            val step = it.nextDouble()
                            SendMathPacket(packetID, number, location, type, value, step)
                        }
                        else -> error("unknown -> packet send ...")
                    }
                }
                "remove" -> {
                    val isPlayer = try {
                        it.mark()
                        it.expect("player")
                        true
                    } catch (ex: Exception) {
                        false
                    }
                    val packetID = it.nextToken()
                    it.mark()
                    it.expect("where")
                    val loc = it.next(ArgTypes.ACTION)
                    if (it.hasNext()) {
                        it.mark()
                        when (it.expects("range")) {
                            "range" -> RemoveRangePacket(isPlayer, packetID, loc, it.nextDouble())
//                            "type" -> RemoveTypePacket(isPlayer, packetID, loc, it.nextToken(), it.nextDouble(), it.nextDouble())
                            else -> error("unknown shell: packet remove")
                        }
                    }else RemoveRangePacket(isPlayer, packetID, loc, 0.0)
                }
                else -> error("unknown shell packet")
            }
        }
    }

}