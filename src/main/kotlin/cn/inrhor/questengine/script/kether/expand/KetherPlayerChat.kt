package cn.inrhor.questengine.script.kether.expand

import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class KetherPlayerChat(val type: Type, val message: ParsedAction<*>, val playerMsg: ParsedAction<*>): ScriptAction<Boolean>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
        /*return when (type) {
            Type.ALL -> CompletableFuture.completedFuture((message.toString() == playerMsg.toString()))
            Type.CONTAINS -> CompletableFuture.completedFuture((message.toString() == playerMsg.toString()))
        }*/
        /*return frame.newFrame(message).run<String>().thenAccept { e ->
            frame.newFrame(playerMsg).run<String>().thenAccept {
                when (type) {
                    Type.ALL -> if (e != it) return@thenAccept
                    Type.CONTAINS -> if (!it.contains(e)) return@thenAccept
                }
            }
        }*/
        return CompletableFuture<Boolean>().also {
            frame.newFrame(message).run<String>().thenAccept { tg ->
                frame.newFrame(playerMsg).run<String>().thenAccept { pm ->
                    it.complete(check(tg, pm))
                }
            }
        }
    }

    fun check(target: Any?, pMsg: Any?): Boolean {
        return when (type) {
            Type.ALL -> (target.toString() == pMsg.toString())
            Type.CONTAINS -> (pMsg.toString().contains(target.toString()))
        }
    }

    enum class Type {
        ALL, CONTAINS
    }

    /*
     * msgMatch type all/contains is *... *...
     */
    internal object Parser {
        @KetherParser(["msgMatch"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            it.mark()
            it.expect("type")
            val unit = try {
                when (val type = it.nextToken()) {
                    "all" -> Type.ALL
                    "contains" -> Type.CONTAINS
                    else -> throw KetherError.CUSTOM.create("未知内容类型: $type")
                }
            } catch (ignored: Exception) {
                it.reset()
                Type.CONTAINS
            }
            it.mark()
            it.expect("is")
            KetherPlayerChat(unit, it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION))
        }
    }

}