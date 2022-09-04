package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.script.kether.player
import cn.inrhor.questengine.utlis.bukkit.InvSlot
import cn.inrhor.questengine.utlis.bukkit.ItemMatch
import taboolib.common5.Demand
import taboolib.module.kether.*
import taboolib.library.kether.*
import java.util.concurrent.CompletableFuture

class KetherItem {

    class CheckInv(val type: String, val item: ParsedAction<*>): ScriptAction<Boolean>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            return frame.newFrame(item).run<Any>().thenApply {
                val player = frame.player()
                ItemMatch(Demand(it.toString())).slotHas(player, InvSlot.valueOf(type.uppercase()))
            }
        }
    }

    class TakeInv(val item: ParsedAction<*>): ScriptAction<Boolean>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            return frame.newFrame(item).run<Any>().thenApply {
                val player = frame.player()
                ItemMatch(Demand(it.toString())).slotHas(player, take = true)
            }
        }
    }

    internal object Parser {
        @KetherParser(["itemCheck"])
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