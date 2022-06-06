package cn.inrhor.questengine.script.kether.expand.control

import cn.inrhor.questengine.api.quest.control.ControlPriority
import cn.inrhor.questengine.api.quest.control.toControlPriority
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.manager.ControlManager
import cn.inrhor.questengine.common.quest.manager.RunLogType
import cn.inrhor.questengine.script.kether.*
import cn.inrhor.questengine.utlis.newLineList
import taboolib.library.kether.ArgTypes
import taboolib.module.kether.*
import taboolib.module.kether.scriptParser

object ActionControl {

    /*
     * control select [controlID]
     * control index [index]
     */
    internal object Parser {

        @KetherParser(["control"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            it.switch {
                case("select") {
                    val action = it.next(ArgTypes.ACTION)
                    actionNow {
                        newFrame(action).run<Any>().thenAccept { a ->
                            variables().set(ActionSelect.ID.variable[6], a.toString())
                        }
                    }
                }
                case("index") {
                    actionNow {
                        val questID = selectQuestID()
                        val innerID = selectInnerID()
                        val controlID = ControlManager.generateControlID(questID, innerID, selectControlID())
                        val cModule = ControlManager.getControlModule(controlID)
                        if (cModule != null) {
                            if (ControlManager.runLogType(controlID) != RunLogType.DISABLE) {
                                val list = cModule.control(questID, innerID).newLineList()
                                if (list.size > index) {
                                    runEval(player(), list[index])
                                }
                            }
                        }
                    }
                }
                case("end") {
                    it.expect("level")
                    val level = it.nextInt().toControlPriority()
                    actionNow {
                        val pD = DataStorage.getPlayerData(player())
                        val controlData =  pD.controlQueue
                        if (level == ControlPriority.HIGHEST) {
                            controlData.highestQueue(player(), selectControlID())
                        }else {
                            controlData.removeNormal(player(), selectControlID())
                        }
                    }
                }
            }
        }
    }

}