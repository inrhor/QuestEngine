package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.common.dialog.animation.text.type.TextWrite
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class KetherEmptyWrite(val delay: Int, val speedWrite: Int, val sendChat: Boolean, val text: String) : ScriptAction<TextWrite>() {

    override fun run(frame: ScriptFrame): CompletableFuture<TextWrite> {
        val referLocation = CompletableFuture<TextWrite>()
        referLocation.complete(
            TextWrite(
                delay,
                speedWrite,
                text,
                TextWrite.Type.EMPTYWRITE,
                sendChat
            )
        )
        return referLocation
    }

    internal object Parser {
        @KetherParser(["emptyWrite"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            it.mark()
            val delay = it.nextInt()
            val speedWrite = it.nextInt()
            val sendChat = try {
                it.mark()
                it.expect("true")
                true
            } catch (ex: Exception) {
                false
            }
            val text = it.nextToken()
            KetherEmptyWrite(delay, speedWrite, sendChat, text)
        }
    }
}