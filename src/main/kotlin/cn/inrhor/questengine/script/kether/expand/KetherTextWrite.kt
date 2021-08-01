package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.common.dialog.animation.text.type.TextWrite
import taboolib.common.platform.info
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class KetherTextWrite(val delay: Int, val speedWrite: Int, val text: String) : ScriptAction<TextWrite>() {

    override fun run(frame: ScriptFrame): CompletableFuture<TextWrite> {
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

    internal object Parser {
        @KetherParser(["textWrite"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            it.mark()
            val delay = it.nextInt()
            val speedWrite = it.nextInt()
            val text = it.nextToken()
            info("Kether>>>>> $delay  $speedWrite  $text")
            KetherTextWrite(delay, speedWrite, text)
        }
    }
}