package cn.inrhor.questengine.common.script.kether.expand

import cn.inrhor.questengine.utlis.location.FixedLocation
import cn.inrhor.questengine.utlis.location.LocationTool
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
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
        fun parser() = ScriptParser.parser {
            val offset = LocationTool().getOffsetType(it)
            val multiply = it.nextDouble()
            val height = it.nextDouble()
            KetherFixedLocation(offset, multiply, height)
        }
    }
}