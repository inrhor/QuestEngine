package cn.inrhor.questengine.script.kether.expand.control

import cn.inrhor.questengine.api.destroyEntity
import cn.inrhor.questengine.common.packet.PacketManager
import cn.inrhor.questengine.common.packet.PacketSpawner
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.common.util.Location
import openapi.kether.*
import taboolib.module.effect.Arc
import taboolib.module.effect.Circle
import taboolib.module.effect.ParticleSpawner
import taboolib.module.effect.Polygon
import taboolib.module.kether.*
import taboolib.module.kether.scriptParser
import taboolib.platform.util.toBukkitLocation
import java.util.concurrent.CompletableFuture

enum class Type {
    SEND, REMOVE, SENDMATH
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
     * packet sendMath id where location [location] type [value] [step]
     */
    class SendMathPacket(val packetID: String, val location: ParsedAction<*>,
                         val type: String, val value: Double, val step: Double): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(location).run<Location>().thenAccept {
                val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
                val t = type.lowercase()
                val spawner = PacketSpawner(player.cast(), packetID)
                if (t == "circle") {
                    Circle(it, value, step, spawner).spawner.spawn(it)
                }else if (t == "polygon") {
                    Polygon(value.toInt(), it, step, spawner).spawner.spawn(it)
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
            val id = m.entityID
            destroyEntity(viewers, id)
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
            it.mark()
            when (action) {
                Type.SEND -> {
                    it.expect("where")
                    SendPacket(
                        it.nextToken(),
                        it.run {
                            it.mark()
                            it.expect("where")
                            it.next(ArgTypes.ACTION)
                        })
                }
                Type.SENDMATH -> {
                    SendMathPacket(
                        it.nextToken(),
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
                }
                else -> {
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
            }
        }
    }

}