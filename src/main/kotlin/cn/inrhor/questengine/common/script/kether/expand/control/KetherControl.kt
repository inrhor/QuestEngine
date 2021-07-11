package cn.inrhor.questengine.common.script.kether.expand.control

import io.izzel.taboolib.kotlin.kether.Kether.expects
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import java.util.concurrent.CompletableFuture

class KetherControl {

    /**
     * wait type time
     */
    class WaitTime(val time: Int): QuestAction<Int>() {
        override fun process(frame: QuestContext.Frame): CompletableFuture<Int> {
            val wait = CompletableFuture<Int>()
            wait.complete(time)
            return wait
        }
    }

    companion object {
        @KetherParser(["wait"])
        fun parser() = ScriptParser.parser {
            when (it.expects("s", "minute")) {
                "s" -> WaitTime(it.nextInt())
                "minute" -> WaitTime(it.nextInt()*60)
                else -> error("unknown type")
            }
        }
    }

}