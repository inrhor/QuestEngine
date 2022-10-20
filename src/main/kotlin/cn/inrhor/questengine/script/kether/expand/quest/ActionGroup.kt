package cn.inrhor.questengine.script.kether.expand.quest

import cn.inrhor.questengine.common.quest.manager.QuestManager.getGroupFrame
import cn.inrhor.questengine.script.kether.selectGroupID
import taboolib.library.kether.ArgTypes
import taboolib.module.kether.KetherParser
import taboolib.module.kether.actionNow
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

object ActionGroup {

    @KetherParser(["group"], shared = true)
    fun parser() = scriptParser {
        it.switch {
            case("select") {
                val quest = it.next(ArgTypes.ACTION)
                actionNow {
                    newFrame(quest).run<Any>().thenAccept { a ->
                        variables().set("@QenGroupID", a.toString())
                    }
                }
            }
            case("name") {
                actionNow {
                    selectGroupID().getGroupFrame()?.name?: "null"
                }
            }
        }
    }

}