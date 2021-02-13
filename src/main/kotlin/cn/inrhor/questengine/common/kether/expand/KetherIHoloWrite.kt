package cn.inrhor.questengine.common.kether.expand

import cn.inrhor.questengine.common.dialog.animation.text.type.HoloWrite
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import java.util.concurrent.CompletableFuture

class KetherIHoloWrite(
    val delay: Int,
    val speedWrite: Int,
    val text: String
) : QuestAction<HoloWrite>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<HoloWrite>? {
        val fixedLocation = CompletableFuture<HoloWrite>()
        fixedLocation.complete(
            HoloWrite(
                delay,
                speedWrite,
                text
            )
        )
        return fixedLocation
    }

    companion object {
        fun parser() = ScriptParser.parser {
            val delay = it.nextInt()
            val speedWrite = it.nextInt()
            val text = it.nextToken()
            KetherIHoloWrite(delay, speedWrite, text)
        }
    }
}