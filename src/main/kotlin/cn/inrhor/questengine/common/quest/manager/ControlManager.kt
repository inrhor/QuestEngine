package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.api.quest.module.inner.QuestControl
import cn.inrhor.questengine.common.database.data.ControlData
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.PlayerData
import cn.inrhor.questengine.common.database.data.quest.QuestControlData
import cn.inrhor.questengine.common.database.data.quest.QuestInnerData
import cn.inrhor.questengine.script.kether.runEval
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
        val cModule = mModule.control

        cModule.forEach {
            val pri = it.level
            val controlID = it.id
            val qcData = QuestControlData(player, controlData,
                controlID, pri, it.script)
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
        val log = controlModule.log

        val sp = controlID.split("-")
        val questID = sp[0]
        val innerID = sp[1]

        var runLine = line
        var runWaitTime = waitTime

        val logType = log.type.lowercase()

        if (logType.startsWith("index ")) {
            val spt = logType.split(" ")
            runLine = spt[1].toInt()
            runWaitTime = 0
        }
        val controlData = QuestControlData(player, cData,
            controlID, controlModule.level, controlModule.script, runLine, runWaitTime)
        cData.addControl(controlID, controlData)
        if (log.enable) {
            runEval(player, log.returnReCall(questID, innerID, priority))
        }
    }

    fun getControlModule(controlID: String): QuestControl? {
        val sp = controlID.split("-")
        val questID = sp[0]
        val innerQuestID = sp[1]
        val mModule = QuestManager.getInnerQuestModule(questID, innerQuestID) ?: return null
        mModule.control.forEach {
            if (it.id == controlID) return it
        }
        return null
    }

    fun generateControlID(questID: String, innerQuestID: String, id: String): String {
        return "$questID-$innerQuestID-$id"
    }

    fun runLogType(controlID: String): RunLogType {
        val module = getControlModule(controlID) ?: return RunLogType.DISABLE
        val log = module.log
        if (!log.enable) return RunLogType.DISABLE
        val sp = log.type.split(" ")
        when (sp[0]) {
            "restart", "index" ->  return RunLogType.RESTART
            "memory" ->  return RunLogType.MEMORY
        }
        return RunLogType.DISABLE
    }

}

enum class RunLogType() {
    DISABLE, RESTART, MEMORY
}