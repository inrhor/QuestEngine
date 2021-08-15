package cn.inrhor.questengine.script.kether.expand

import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class KetherStrMatch(val type: Type, val target: ParsedAction<*>, val source: ParsedAction<*>): ScriptAction<Boolean>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
        return CompletableFuture<Boolean>().also {
            frame.newFrame(target).run<String>().thenAccept { tg ->
                frame.newFrame(source).run<String>().thenAccept { so ->
                    it.complete(check(tg, so))
                }
            }
        }
    }

    fun check(target: Any?, pMsg: Any?): Boolean {
        return when (type) {
            Type.ALL -> target.toString() == pMsg.toString()
            Type.CONTAINS -> pMsg.toString().contains(target.toString())
        }
    }

    enum class Type {
        ALL, CONTAINS
    }

    /*
     * strMatch type all/contains is *... *...
     */
    internal object Parser {
        @KetherParser(["strMatch"], namespace = "QuestEngine")
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
            KetherStrMatch(unit, it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION))
        }
    }

}