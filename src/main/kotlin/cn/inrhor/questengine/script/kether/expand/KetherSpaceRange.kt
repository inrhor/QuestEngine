package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.utlis.location.LocationTool
import org.bukkit.Location
import taboolib.common.platform.ProxyPlayer
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.platform.util.toBukkitLocation
import java.util.concurrent.CompletableFuture

class KetherSpaceRange(val x: Double, val y: Double, val z: Double, val location: ParsedAction<*>): ScriptAction<Boolean>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
        return CompletableFuture<Boolean>().also { future ->
            frame.newFrame(location).run<Location>().thenAccept {
                val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
                val loc = player.location
                future.complete(LocationTool.inLoc(loc.toBukkitLocation(), it, x, y, z))
            }
        }
    }

    internal object Parser {
        @KetherParser(["spaceRange"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            val x = it.nextDouble()
            val y = it.nextDouble()
            val z = it.nextDouble()
            it.mark()
            it.expect("where")
            KetherSpaceRange(x, y, z, it.next(ArgTypes.ACTION))
        }
    }

}