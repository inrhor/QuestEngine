package cn.inrhor.questengine.script.kether.expand.quest

import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.enum.toState
import cn.inrhor.questengine.script.kether.*
import taboolib.library.kether.ArgTypes
import taboolib.module.kether.KetherParser
import taboolib.module.kether.actionNow
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

object ActionQuest {

    @KetherParser(["quest"], shared = true)
    fun parser() = scriptParser {
        it.switch {
            /**
             * 选择任务
             * quest select [questID]
             * quest select useUid [questUUID]
             */
            case("select") {
                val type = selectType()
                val quest = it.next(ArgTypes.ACTION)
                actionNow {
                    newFrame(quest).run<Any>().thenAccept { a ->
                        variables().set(type.variable[0], a.toString())
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
                    QuestManager.acceptQuest(player(), selectQuestID())
                }
            }
            /**
             * 前提：
             * 粗略检索：quest select [questID]
             * 精准检索：quest select useUid [questUUID]
             *
             * quest quit
             * 放弃任务并清空任务数据
             */
            case("quit") {
                actionNow {
                    when (selectType()) {
                        ActionSelect.ID -> QuestManager.quitQuest(player(), selectQuestID())
                        else -> QuestManager.quitQuest(player(), selectQuestUid())
                    }
                }
            }
            case("finish") {
                actionNow {
                    when (selectType()) {
                        ActionSelect.ID -> QuestManager.quitQuest(player(), selectQuestID())
                        else -> QuestManager.quitQuest(player(), selectQuestUid())
                    }
                }
            }
            case("state") {
                actionNow {
                    QuestManager.setQuestState(player(), selectQuestID(), it.nextToken().toState())
                }
            }
        }
    }

}