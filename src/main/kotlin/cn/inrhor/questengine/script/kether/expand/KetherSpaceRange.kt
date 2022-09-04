package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.script.kether.player
import cn.inrhor.questengine.utlis.location.LocationTool
import org.bukkit.Location
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class KetherSpaceRange(val x: Double, val y: Double, val z: Double, val location: ParsedAction<*>): ScriptAction<Boolean>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
        return CompletableFuture<Boolean>().also { future ->
            frame.newFrame(location).run<Location>().thenAccept {
                val player = frame.player()
                val loc = player.location
                future.complete(LocationTool.inLoc(loc, it, x, y, z))
            }
        }
    }

    internal object Parser {
        @KetherParser(["spaceRange"])
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