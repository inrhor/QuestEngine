package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.utlis.location.ReferLocation
import cn.inrhor.questengine.utlis.location.LocationTool
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class KetherReferLocation(
    val offset: Float,
    val multiply: Double,
    val height: Double
) : ScriptAction<ReferLocation>() {

    override fun run(frame: ScriptFrame): CompletableFuture<ReferLocation> {
        val referLocation = CompletableFuture<ReferLocation>()
        referLocation.complete(
            ReferLocation(
                offset,
                multiply,
                height
            )
        )
        return referLocation
    }

    /*
        addLoc/initLoc dir [dir] add [multiply] [height]
     */
    internal object Parser {
        @KetherParser(["addLoc", "initLoc"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            it.mark()
            it.expect("dir")
            val offset = LocationTool.getOffsetType(it)
            it.mark()
            it.expect("add")
            val multiply = it.nextDouble()
            val height = it.nextDouble()
            KetherReferLocation(offset, multiply, height)
        }
    }
}