package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.utlis.bukkit.ItemCheck
import taboolib.module.kether.*
import taboolib.common.platform.ProxyPlayer
import taboolib.library.kether.*
import java.util.concurrent.CompletableFuture

class KetherItem {

    class CheckInv(val type: String, val item: ParsedAction<*>): ScriptAction<Boolean>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            return frame.newFrame(item).run<Any>().thenApply {
                val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
                val item = ItemCheck.eval(it.toString())
                when (type.lowercase()) {
                    "all" -> item.invHas(player.cast(), false)
                    "mainhand" -> item.isMainHand(player.cast(), false)
                    else -> false
                }
            }
        }
    }

    class TakeInv(val item: ParsedAction<*>): ScriptAction<Boolean>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            return frame.newFrame(item).run<Any>().thenApply {
                val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
                ItemCheck.eval(it.toString()).invHas(player.cast(), true)
            }
        }
    }

    internal object Parser {
        @KetherParser(["itemCheck"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            it.mark()
            when (it.expects("inv", "take")) {
                "inv" -> CheckInv(it.nextToken(), it.next(ArgTypes.ACTION))
                "take" -> TakeInv(it.next(ArgTypes.ACTION))
                else -> error("unknown type")
            }
        }

    }

}