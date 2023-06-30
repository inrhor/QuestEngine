package cn.inrhor.questengine.script.kether.expand.quest

import cn.inrhor.questengine.api.manager.DataManager.targetData
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.quest.manager.QuestManager.finishTarget
import cn.inrhor.questengine.common.quest.manager.QuestManager.getTargetFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager.trackTarget
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.script.kether.player
import cn.inrhor.questengine.script.kether.selectQuestID
import cn.inrhor.questengine.script.kether.selectTargetID
import taboolib.library.kether.ArgTypes
import taboolib.module.kether.*

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
            case("id") {
                actionNow {
                    selectTargetID()
                }
            }
            case("schedule") {
                actionNow {
                    player().targetData(selectQuestID(), selectTargetID())?.schedule?: 0
                }
            }
            case("track") {
                actionNow {
                    player().trackTarget(selectQuestID(), selectTargetID())
                }
            }
            case("finish") {
                actionNow {
                    player().finishTarget(selectQuestID(), selectTargetID())
                }
            }
            case("state") {
                try {
                    it.mark()
                    when (it.expects("lang", "set")) {
                        "lang" -> {
                            actionNow {
                                (player().targetData(selectQuestID(), selectTargetID())?.state?: StateType.NOT_ACCEPT).toUnit(player())
                            }
                        }
                        "set" -> {
                            val s = it.nextToken()
                            actionNow {
                             TargetManager.toggleTarget(player(), selectQuestID(), selectTargetID(), StateType.valueOf(s.uppercase()))
                            }
                        }
                        else -> {
                            actionNow {
                                (player().targetData(selectQuestID(), selectTargetID())?.state?: StateType.NOT_ACCEPT).toString()
                            }
                        }
                    }
                }catch (ex: Exception) {
                    it.reset()
                    actionNow {
                        (player().targetData(selectQuestID(), selectTargetID())?.state?: StateType.NOT_ACCEPT).toString()
                    }
                }
            }
            case("note") {
                actionNow {
                    selectTargetID().getTargetFrame(selectQuestID())?.description?.joinToString("\\n")?: ""
                }
            }
        }
    }

}