package cn.inrhor.questengine.script.kether.expand.quest

import cn.inrhor.questengine.script.kether.ActionSelect
import taboolib.library.kether.ArgTypes
import taboolib.module.kether.KetherParser
import taboolib.module.kether.actionNow
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

object ActionTarget {

    @KetherParser(["innerTarget"], shared = true)
    fun parser() = scriptParser {
        it.switch {
            case("select") {
                val inner = it.next(ArgTypes.ACTION)
                actionNow {
                    newFrame(inner).run<Any>().thenAccept { a ->
                        variables().set(ActionSelect.ID.variable[2], a.toString())
                    }
                }
            }
        }
    }

}