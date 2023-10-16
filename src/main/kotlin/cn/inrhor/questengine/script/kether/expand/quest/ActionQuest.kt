package cn.inrhor.questengine.script.kether.expand.quest

import cn.inrhor.questengine.api.manager.DataManager.questData
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.quest.manager.QuestManager.acceptCoolDown
import cn.inrhor.questengine.common.quest.manager.QuestManager.acceptQuest
import cn.inrhor.questengine.common.quest.manager.QuestManager.coolDown
import cn.inrhor.questengine.common.quest.manager.QuestManager.finishQuest
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager.quitQuest
import cn.inrhor.questengine.common.quest.manager.QuestManager.trackQuest
import cn.inrhor.questengine.script.kether.*
import cn.inrhor.questengine.script.kether.player
import cn.inrhor.questengine.utlis.time.remainDate
import taboolib.library.kether.ArgTypes
import taboolib.module.kether.*
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
            case("track") {
                actionNow {
                    player().trackQuest(selectQuestID())
                }
            }
            case("name") {
                actionNow {
                    selectQuestID().getQuestFrame()?.name?: "null"
                }
            }
            case("state") {
                try {
                    it.mark()
                    it.expect("lang")
                    actionNow {
                        (player().questData(selectQuestID())?.state?: StateType.NOT_ACCEPT).toUnit(player())
                    }
                }catch (ex: Exception) {
                    it.reset()
                    actionNow {
                        (player().questData(selectQuestID())?.state?: StateType.NOT_ACCEPT).toString()
                    }
                }
            }
            case("limitTime") {
                actionNow {
                    selectQuestID().getQuestFrame()?.time?.endDate?.remainDate(player(), player().questData(selectQuestID())?.state?: StateType.NOT_ACCEPT)?: player().asLangText("QUEST-ALWAYS")
                }
            }
            case("note") {
                actionNow {
                    selectQuestID().getQuestFrame()?.note?.joinToString("\\n")?: ""
                }
            }
            case("coolDown") {
                try {
                    it.mark()
                    it.expect("lang")
                    actionNow {
                        player().questData(selectQuestID())?.coolDown(player())?: player().asLangText("COOL_DOWN_OK")
                    }
                }catch (ex: Exception) {
                    it.reset()
                    actionNow {
                        player().acceptCoolDown(selectQuestID())
                    }
                }
            }
        }
    }

}