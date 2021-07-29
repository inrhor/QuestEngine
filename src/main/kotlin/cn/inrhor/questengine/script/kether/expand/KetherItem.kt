package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.utlis.bukkit.ItemCheck
import cn.inrhor.questengine.utlis.public.MsgUtil
import io.izzel.taboolib.kotlin.kether.Kether.expects
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.common.api.ParsedAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.script
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class KetherItem {

    class CheckInv(val item: ParsedAction<*>): QuestAction<Boolean>() {
        override fun process(frame: QuestContext.Frame): CompletableFuture<Boolean> {
            return frame.newFrame(item).run<Any>().thenApply {
                MsgUtil.send("eval  eval  $it")
                val player = frame.script().sender as? Player ?: error("unknown player")
                ItemCheck.eval(it.toString()).invHas(player, false)
            }
        }
    }

    class TakeInv(val item: ParsedAction<*>): QuestAction<Boolean>() {
        override fun process(frame: QuestContext.Frame): CompletableFuture<Boolean> {
            return frame.newFrame(item).run<Any>().thenApply {
                MsgUtil.send("eval  eval  $it")
                val player = frame.script().sender as? Player ?: error("unknown player")
                ItemCheck.eval(it.toString()).invHas(player, true)
            }
        }
    }

    companion object {
        @KetherParser(["itemCheck"], namespace = "QuestEngine")
        fun parser() = ScriptParser.parser {
            when (it.expects("inv", "take")) {
                "inv" -> CheckInv(it.next(ArgTypes.ACTION))
                "take" -> TakeInv(it.next(ArgTypes.ACTION))
                else -> error("unknown type")
            }
        }

    }

}