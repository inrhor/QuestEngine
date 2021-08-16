package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.api.quest.control.*
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

    fun saveControl(player: Player, controlData: ControlData, questInnerData: QuestInnerData) {
        val questID = questInnerData.questID
        val innerQuestID = questInnerData.innerQuestID
        val mModule = QuestManager.getInnerQuestModule(questID, innerQuestID) ?: return
        val cModule = mModule.questControls

        cModule.forEach {
            val pri = it.priority
            val controlID = it.controlID
            val qcData = QuestControlData(player, controlData,
                controlID, pri, it.controls)
            controlData.addControl(controlID, qcData)
        }
    }

    /**
     * 拉取数据时存储控制模块，一般情况使用 saveControl 方法
     */
    fun pullControl(player: Player, controlID: String, priority: String, line: Int, waitTime: Int) {
        val uuid = player.uniqueId
        val pDate = DataStorage.getPlayerData(uuid)
        val cData = pDate.controlData

        val controlModule = getControlModule(controlID)?: return
        val log = controlModule.logOpen

        val sp = controlID.split("-")
        val questID = sp[0]
        val innerID = sp[1]

        var runLine = line
        var runWaitTime = waitTime

        val logType = log.logType.lowercase()

        if (logType.startsWith("index ")) {
            val spt = logType.split(" ")
            runLine = spt[1].toInt()
            runWaitTime = 0
        }
        val controlData = QuestControlData(player, cData,
            controlID, controlModule.priority, controlModule.controls, runLine, runWaitTime)
        cData.addControl(controlID, controlData)
        if (log.isEnable) {
            eval(player, log.returnReKether(questID, innerID, priority))
        }
    }

    fun getControlModule(controlID: String): QuestControlOpen? {
        val sp = controlID.split("-")
        val questID = sp[0]
        val innerQuestID = sp[1]
        val mModule = QuestManager.getInnerQuestModule(questID, innerQuestID) ?: return null
        mModule.questControls.forEach {
            if (it.controlID == controlID) return it
        }
        return null
    }

    fun generateControlID(questID: String, innerQuestID: String, priority: String): String {
        return "$questID-$innerQuestID-$priority"
    }

    fun generateControlID(questID: String, innerQuestID: String, priority: ControlPriority): String {
        return "$questID-$innerQuestID-${priority.toStr()}"
    }

    fun runLogType(controlID: String): RunLogType {
        val module = getControlModule(controlID) ?: return RunLogType.DISABLE
        val log = module.logOpen
        if (!log.isEnable) return RunLogType.DISABLE
        when (log.logType) {
            "restart", "index" ->  return RunLogType.RESTART
            "memory" ->  return RunLogType.MEMORY
        }
        return RunLogType.DISABLE
    }

}

enum class RunLogType() {
    DISABLE, RESTART, MEMORY
}