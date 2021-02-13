package cn.inrhor.questengine.common.kether.expand

import cn.inrhor.questengine.common.dialog.location.FixedLocation
import io.izzel.taboolib.kotlin.kether.KetherError
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
            val offset = when (it.nextToken().toLowerCase()) {
                "left" -> -90F
                "right" -> 90F
                "behind" -> 180F
                else -> throw KetherError.CUSTOM.create("未知方向类型")
            }
            val multiply = it.nextDouble()
            val height = it.nextDouble()
            KetherFixedLocation(offset, multiply, height)
        }
    }
}