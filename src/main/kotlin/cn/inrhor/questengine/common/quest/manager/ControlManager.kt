package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.api.quest.ControlPriority
import cn.inrhor.questengine.api.quest.QuestControlModule
import cn.inrhor.questengine.api.quest.toStr
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
        saveControl(player, pData.controlData, questInnerData)
    }

    /**
     * 存储控制模块
     */
    fun saveControl(player: Player, controlData: ControlData, questInnerData: QuestInnerData) {
        val questID = questInnerData.questID
        val innerQuestID = questInnerData.innerQuestID
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

        var runLine = line
        var runWaitTime = waitTime

        if (priority == "highest") {
            if (log.highestLogType.startsWith("index ")) {
                val spt = log.highestLogType.split(" ")
                runLine = spt[1].toInt()
                runWaitTime = 0
            }
            val controlData = QuestControlData(player, cData,
                controlID, ControlPriority.HIGHEST, controlModule.highestControl, runLine, runWaitTime)
            cData.addHighest(uuid, controlID, controlData)
            if (log.highestLogEnable) {
                eval(player, log.returnHighestReKether(questID, innerID, priority))
            }
        }else {
            if (log.normalLogType.startsWith("index ")) {
                val spt = log.normalLogType.split(" ")
                runLine = spt[1].toInt()
                runWaitTime = 0
            }
            val controlData = QuestControlData(player, cData,
                controlID, ControlPriority.NORMAL, controlModule.normalControl, runLine, runWaitTime)
            cData.addCommon(uuid, controlID, controlData)
            if (log.normalLogEnable) {
                eval(player, log.returnNormalReKether(questID, innerID, priority))
            }
        }
    }

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

    fun generateControlID(questID: String, innerQuestID: String, priority: ControlPriority): String {
        return "$questID-$innerQuestID-${priority.toStr()}"
    }

    fun runLogType(controlID: String, priority: ControlPriority): RunLogType {
        val module = getControlModule(controlID) ?: return RunLogType.DISABLE
        val log = module.logModule
        if (priority == ControlPriority.HIGHEST) {
            if (!log.highestLogEnable) return RunLogType.DISABLE
            when (log.highestLogType) {
                "restart", "index" ->  return RunLogType.RESTART
                "memory" ->  return RunLogType.MEMORY
            }
        } else {
            if (!log.normalLogEnable) return RunLogType.DISABLE
            when (log.normalLogType) {
                "restart", "index" ->  return RunLogType.RESTART
                "memory" ->  return RunLogType.MEMORY
            }
        }
        return RunLogType.DISABLE
    }

}

enum class RunLogType() {
    DISABLE, RESTART, MEMORY
}