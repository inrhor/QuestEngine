package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.common.dialog.animation.text.type.TextWrite
import taboolib.library.kether.*
import taboolib.module.kether.KetherParser
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class KetherTextWrite(val delay: Int, val speedWrite: Int, val text: String) : QuestAction<TextWrite>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<TextWrite> {
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
        @KetherParser(["textWrite"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            val delay = it.nextInt()
            val speedWrite = it.nextInt()
            val text = it.nextToken()
            KetherTextWrite(delay, speedWrite, text)
        }
    }
}