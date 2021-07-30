package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.utlis.location.FixedLocation
import cn.inrhor.questengine.utlis.location.LocationTool
import taboolib.library.kether.*
import taboolib.module.kether.KetherParser
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class KetherFixedLocation(
    val offset: Float,
    val multiply: Double,
    val height: Double
) : QuestAction<FixedLocation>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<FixedLocation>? {
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

    companion object {
        @KetherParser(["addLoc", "initLoc"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            val offset = LocationTool().getOffsetType(it)
            val multiply = it.nextDouble()
            val height = it.nextDouble()
            KetherFixedLocation(offset, multiply, height)
        }
    }
}