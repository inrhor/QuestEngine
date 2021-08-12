package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.api.quest.ControlPriority
import cn.inrhor.questengine.api.quest.QuestControlModule
import cn.inrhor.questengine.common.database.data.ControlData
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.PlayerData
import cn.inrhor.questengine.common.database.data.quest.QuestControlData
import cn.inrhor.questengine.common.database.data.quest.QuestInnerData
import cn.inrhor.questengine.script.kether.eval
import org.bukkit.entity.Player

object ControlManager {

    /**
     * 存储控制模块
     */
    fun saveControl(player: Player, pData: PlayerData, questInnerData: QuestInnerData) {
        val questID = questInnerData.questID
        val innerQuestID = questInnerData.innerQuestID
        saveControl(player, pData.controlData, questID, innerQuestID)
    }

    /**
     * 存储控制模块
     */
    fun saveControl(player: Player, controlData: ControlData, questID: String, innerQuestID: String) {
        val mModule = QuestManager.getInnerQuestModule(questID, innerQuestID) ?: return
        val cModule = mModule.questControl
        val highestID = cModule.highestID
        val normalID = cModule.normalID
        if (highestID == "" || normalID == "") return

        val hControl = cModule.highestControl
        val nControl = cModule.normalControl

        val hControlData = QuestControlData(player, controlData,
            highestID, ControlPriority.HIGHEST, hControl)
        val nControlData = QuestControlData(player, controlData,
            normalID, ControlPriority.NORMAL, nControl)

        controlData.addControl(player.uniqueId, highestID, normalID, hControlData, nControlData)
    }

    /**
     * 拉取数据时存储控制模块，一般情况使用 saveControl 方法
     */
    fun pullControl(player: Player, controlID: String, priority: String, line: Int, waitTime: Int) {
        val uuid = player.uniqueId
        val pDate = DataStorage.getPlayerData(uuid)
        val cData = pDate.controlData

        val controlModule = getControlModule(controlID)?: return
        val log = controlModule.logModule

        val sp = controlID.split("-")
        val questID = sp[0]
        val innerID = sp[1]

        if (priority == "highest") {
            val controlData = QuestControlData(player, cData,
                controlID, ControlPriority.HIGHEST, controlModule.highestControl, line, waitTime)
            cData.addHighest(uuid, controlID, controlData)
            if (log.highestLogEnable) {
//                eval(player, log.returnHighestReKether(questID, innerID, priority))
            }
        }else {
            val controlData = QuestControlData(player, cData,
                controlID, ControlPriority.NORMAL, controlModule.normalControl, line, waitTime)
            cData.addCommon(uuid, controlID, controlData)
            if (log.normalLogEnable) {
//                eval(player, log.returnNormalReKether(questID, innerID, priority))
            }
        }
    }

    /*fun logTypeRun(logType: String, player: Player, controlEval: MutableList<String>) {
        when (logType) {
            "restart" ->
            "momery" ->
        }
    }*/

    fun getControlModule(controlID: String): QuestControlModule? {
        val sp = controlID.split("-")
        val questID = sp[0]
        val innerQuestID = sp[1]
        val mModule = QuestManager.getInnerQuestModule(questID, innerQuestID) ?: return null
        return mModule.questControl
    }

    fun generateControlID(questID: String, innerQuestID: String, priority: String): String {
        return "$questID-$innerQuestID-$priority"
    }

    fun isEnable(controlID: String, priority: ControlPriority): Boolean {
        val module = getControlModule(controlID)?: return false
        val log = module.logModule
        if (priority == ControlPriority.HIGHEST) {
            if (log.highestLogEnable) return true
        }else {
            if (log.normalLogEnable) return true
        }
        return false
    }

}