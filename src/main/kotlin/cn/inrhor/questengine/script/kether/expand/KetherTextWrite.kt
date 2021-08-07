package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.common.dialog.animation.text.type.TextWrite
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class KetherTextWrite(val delay: Int, val speedWrite: Int, val text: String) : ScriptAction<TextWrite>() {

    override fun run(frame: ScriptFrame): CompletableFuture<TextWrite> {
        val referLocation = CompletableFuture<TextWrite>()
        referLocation.complete(
            TextWrite(
                delay,
                speedWrite,
                text
            )
        )
        return referLocation
    }

    internal object Parser {
        @KetherParser(["textWrite"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            it.mark()
            val delay = it.nextInt()
            val speedWrite = it.nextInt()
            val text = it.nextToken()
            KetherTextWrite(delay, speedWrite, text)
        }
    }
}