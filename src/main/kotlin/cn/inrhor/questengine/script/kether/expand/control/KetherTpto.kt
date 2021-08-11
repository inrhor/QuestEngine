package cn.inrhor.questengine.script.kether.expand.control

import openapi.kether.ArgTypes
import openapi.kether.ParsedAction
import taboolib.common.platform.ProxyPlayer
import taboolib.common.util.Location
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class KetherTpto(val location: ParsedAction<*>): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        return frame.newFrame(location).run<Location>().thenAccept {
            val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
            player.teleport(it)
        }
    }

    internal object Parser {
        @KetherParser(["tpto"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            it.mark()
            it.expect("location")
            KetherTpto(it.next(ArgTypes.ACTION))
        }
    }

}