package cn.inrhor.questengine.script.kether.expand.quest

import cn.inrhor.questengine.common.database.data.targetData
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.script.kether.player
import cn.inrhor.questengine.script.kether.selectQuestID
import cn.inrhor.questengine.script.kether.selectTargetID
import taboolib.library.kether.ArgTypes
import taboolib.module.kether.KetherParser
import taboolib.module.kether.actionNow
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

object ActionTarget {

    @KetherParser(["target"], shared = true)
    fun parser() = scriptParser {
        it.switch {
            case("select") {
                val inner = it.next(ArgTypes.ACTION)
                actionNow {
                    newFrame(inner).run<Any>().thenAccept { a ->
                        variables().set("@QenTargetID", a.toString())
                    }
                }
            }
            case("schedule") {
                actionNow {
                    try {
                        player().targetData(selectQuestID(), selectTargetID()).schedule
                    }catch (ex: Exception) {
                        0
                    }
                }
            }
            case("state") {
                actionNow {
                    try {
                        player().targetData(selectQuestID(), selectTargetID()).state.toUnit(player())
                    }catch (ex: Exception) {
                        StateType.NOT_ACCEPT.toUnit(player())
                    }
                }
            }
        }
    }

}