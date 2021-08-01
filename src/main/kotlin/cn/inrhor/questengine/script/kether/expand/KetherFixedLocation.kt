package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.utlis.location.FixedLocation
import cn.inrhor.questengine.utlis.location.LocationTool
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class KetherFixedLocation(
    val offset: Float,
    val multiply: Double,
    val height: Double
) : ScriptAction<FixedLocation>() {

    override fun run(frame: ScriptFrame): CompletableFuture<FixedLocation> {
        val fixedLocation = CompletableFuture<FixedLocation>()
        fixedLocation.complete(
            FixedLocation(
                offset,
                multiply,
                height
            )
        )
        return fixedLocation
    }

    /*
        addLoc/initLoc dir left add [multiply] [height]
     */
    internal object Parser {
        @KetherParser(["addLoc", "initLoc"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            it.mark()
            it.expect("dir")
            val offset = LocationTool().getOffsetType(it)
            it.mark()
            it.expect("add")
            val multiply = it.nextDouble()
            val height = it.nextDouble()
            KetherFixedLocation(offset, multiply, height)
        }
    }
}