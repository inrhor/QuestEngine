package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.utlis.bukkit.ItemCheck
import org.bukkit.entity.Player
import taboolib.library.kether.*
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class KetherItem {

    class CheckInv(val item: ParsedAction<*>): ScriptAction<Boolean>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            return frame.newFrame(item).run<Any>().thenApply {
                val player = frame.script().sender as? Player ?: error("unknown player")
                ItemCheck.eval(it.toString()).invHas(player, false)
            }
        }
    }

    class TakeInv(val item: ParsedAction<*>): ScriptAction<Boolean>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            return frame.newFrame(item).run<Any>().thenApply {
                val player = frame.script().sender as? Player ?: error("unknown player")
                ItemCheck.eval(it.toString()).invHas(player, true)
            }
        }
    }

    internal object Parser {
        @KetherParser(["itemCheck"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            it.mark()
            when (it.expects("inv", "take")) {
                "inv" -> CheckInv(it.next(ArgTypes.ACTION))
                "take" -> TakeInv(it.next(ArgTypes.ACTION))
                else -> error("unknown type")
            }
        }

    }

}