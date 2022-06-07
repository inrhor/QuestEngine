package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.api.quest.module.inner.QuestControl
import cn.inrhor.questengine.common.database.data.ControlQueue
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.PlayerData
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.script.kether.runEval
import org.bukkit.entity.Player

object ControlManager {

    /**
     * 存储控制模块
     */
    fun saveControl(player: Player, pData: PlayerData, questInnerData: QuestData) {
        saveControl(player, pData.controlQueue, questInnerData)
    }

    fun saveControl(player: Player, controlData: ControlQueue, questInnerData: QuestData) {
        val questID = questInnerData.questID
        val innerQuestID = questInnerData.id
        val mModule = QuestManager.getInnerModule(questID, innerQuestID) ?: return
        val cModule = mModule.control

        cModule.forEach {
            val pri = it.level
            val controlID = generateControlID(questID, innerQuestID, it.id)
            val qcData = ControlData(player, controlID, pri, it.control(questID, innerQuestID))
            controlData.addControl(controlID, qcData)
        }
    }

    /**
     * 拉取数据时存储控制模块，一般情况使用 saveControl 方法
     */
    fun pullControl(player: Player, controlID: String, line: Int,) {
        val uuid = player.uniqueId
        val pDate = DataStorage.getPlayerData(uuid)
        val cData = pDate.controlQueue

        val controlModule = getControlModule(controlID)?: return
        val log = controlModule.log

        val sp = controlID.split("-")
        val questID = sp[0]
        val innerID = sp[1]

        var runLine = line

        val logType = log.type.lowercase()

        if (logType.startsWith("index ")) {
            val spt = logType.split(" ")
            runLine = spt[1].toInt()
        }
        val controlData = ControlData(player, controlID, controlModule.level, controlModule.control(questID, innerID), runLine)
        cData.addControl(controlID, controlData)
        if (log.enable) {
            runEval(player, log.replaceRecall(questID, innerID, controlID))
        }
    }

    fun getControlModule(controlID: String): QuestControl? {
        val sp = controlID.split("-")
        val questID = sp[0]
        val innerQuestID = sp[1]
        val mModule = QuestManager.getInnerModule(questID, innerQuestID) ?: return null
        mModule.control.forEach {
            if (it.id == sp[2]) return it
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