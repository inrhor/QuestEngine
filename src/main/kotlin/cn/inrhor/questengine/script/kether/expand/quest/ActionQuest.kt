package cn.inrhor.questengine.script.kether.expand.quest

import cn.inrhor.questengine.common.database.data.questData
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.quest.manager.QuestManager.acceptQuest
import cn.inrhor.questengine.common.quest.manager.QuestManager.finishQuest
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager.quitQuest
import cn.inrhor.questengine.script.kether.*
import cn.inrhor.questengine.utlis.time.remainDate
import taboolib.library.kether.ArgTypes
import taboolib.module.kether.KetherParser
import taboolib.module.kether.actionNow
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch
import taboolib.platform.util.asLangText

object ActionQuest {

    @KetherParser(["quest"], shared = true)
    fun parser() = scriptParser {
        it.switch {
            /**
             * 选择任务
             * quest select [questID]
             */
            case("select") {
                val quest = it.next(ArgTypes.ACTION)
                actionNow {
                    newFrame(quest).run<Any>().thenAccept { a ->
                        variables().set("@QenQuestID", a.toString())
                    }
                }
            }
            /**
             * 前提：quest select [questID]
             *
             * 接受任务
             * quest accept
             */
            case("accept") {
                actionNow {
                    player().acceptQuest(selectQuestID())
                }
            }
            /**
             * quest select [questID]
             * quest quit
             * 放弃任务并清空任务数据
             */
            case("quit") {
                actionNow {
                    player().quitQuest(selectQuestID())
                }
            }
            case("finish") {
                actionNow {
                    player().finishQuest(selectQuestID())
                }
            }
            case("name") {
                actionNow {
                    selectQuestID().getQuestFrame().name
                }
            }
            case("state") {
                actionNow {
                    try {
                        player().questData(selectQuestID()).state.toUnit(player())
                    }catch (ex: Exception) {
                        StateType.NOT_ACCEPT.toUnit(player())
                    }
                }
            }
            case("limitTime") {
                actionNow {
                    try {
                        selectQuestID().getQuestFrame().time.endDate?.remainDate(player(), player().questData(selectQuestID()).state)
                    }catch (ex: Exception) {
                        player().asLangText("QUEST-ALWAYS")
                    }
                }
            }
        }
    }

}