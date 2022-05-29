package cn.inrhor.questengine.script.kether.expand.quest

import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.script.kether.ActionSelect
import cn.inrhor.questengine.script.kether.player
import cn.inrhor.questengine.script.kether.selectInnerID
import cn.inrhor.questengine.script.kether.selectQuestID
import taboolib.library.kether.ArgTypes
import taboolib.module.kether.KetherParser
import taboolib.module.kether.actionNow
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

object ActionInner {

    @KetherParser(["inner"], shared = true)
    fun parser() = scriptParser {
        it.switch {
            case("select") {
                val inner = it.next(ArgTypes.ACTION)
                actionNow {
                    newFrame(inner).run<Any>().thenAccept { a ->
                        variables().set(ActionSelect.ID.variable[1], a.toString())
                    }
                }
            }
            case("accept") {
                actionNow {
                    QuestManager.acceptInnerQuest(player(), selectQuestID(), selectInnerID(), true)
                }
            }
            case("finish") {
                actionNow {
                    QuestManager.finishInnerQuest(player(), selectQuestID(), selectInnerID())
                }
            }
        }
    }

}