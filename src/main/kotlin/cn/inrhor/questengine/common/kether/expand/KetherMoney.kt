package cn.inrhor.questengine.common.kether.expand

import cn.inrhor.questengine.common.kether.BaseAction
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.common.api.ParsedAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.kether.common.loader.types.ArgTypes
import java.util.concurrent.CompletableFuture

/**
 * 获取数字
 */
class KetherMoney(val money: ParsedAction<*>): BaseAction<Double>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Double> {
        return context.newFrame(money).run<Any>().thenApply {
            0.0
        }
    }

    companion object {
        fun parser() = ScriptParser.parser {
            KetherMoney(it.next(ArgTypes.ACTION))
        }
    }

}