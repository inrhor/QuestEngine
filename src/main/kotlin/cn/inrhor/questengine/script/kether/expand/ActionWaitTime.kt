package cn.inrhor.questengine.script.kether.expand

import taboolib.common.platform.function.submit
import taboolib.library.kether.ArgTypes
import taboolib.module.kether.*
import java.util.concurrent.*

/**
 * 拷贝自TabooLib ActionWait
 */
class ActionWaitTime(val ticks: Long) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        val bukkitTask = submit(delay = ticks) {
            // 如果玩家在等待过程中离线则终止脚本
            if (frame.script().sender?.isOnline() == false) {
                return@submit
            }
            future.complete(null)
        }
        frame.addClosable(AutoCloseable {
            bukkitTask.cancel()
        })
        return future
    }

    internal object Parser {

        @KetherParser(["waitTime"])
        fun parser() = scriptParser {
            ActionWaitTime(it.next(ArgTypes.DURATION).toMillis() / 50L)
        }
    }
}