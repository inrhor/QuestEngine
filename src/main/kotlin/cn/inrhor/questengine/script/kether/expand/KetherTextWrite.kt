package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.common.dialog.animation.text.type.TextWrite
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import java.util.concurrent.CompletableFuture

class KetherTextWrite(
    val delay: Int,
    val speedWrite: Int,
    val text: String
) : QuestAction<TextWrite>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<TextWrite>? {
        val fixedLocation = CompletableFuture<TextWrite>()
        fixedLocation.complete(
            TextWrite(
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
            KetherTextWrite(delay, speedWrite, text)
        }
    }
}