package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.api.quest.control.ControlPriority
import cn.inrhor.questengine.common.database.Database
import org.bukkit.entity.Player
import kotlin.collections.LinkedHashMap

/**
 * 控制模块数据列表
 *
 * @param control 排队运行
 */
class ControlQueue(var control: LinkedHashMap<String, ControlData> = linkedMapOf()) {

    /**
     * /**
     * 添加最高级控制模块并进入队列等待运行
     * 添加普通控制模块并直接运行
    */
     */
    fun addControl(controlID: String, questControlData: ControlData) {
        when (questControlData.priority) {
            ControlPriority.HIGHEST -> addHighest(controlID, questControlData)
            ControlPriority.NORMAL -> addCommon(controlID, questControlData)
        }
    }

    /**
     * 添加最高级控制模块并进入队列等待运行
     */
    fun addHighest(controlID: String, questControlData: ControlData) {
        highestControls[controlID] = questControlData
        if (highestControls.size < 2) {
            questControlData.runScript()
        }
    }

    /**
     * 添加普通控制模块并直接运行
     */
    fun addCommon(controlID: String, questControlData: ControlData) {
        controls[controlID] = questControlData
        questControlData.runScript()
    }

    /**
     * 当前最高级控制模块运行完毕后删除
     * 并进行下一个最高级控制模块
     */
    fun highestQueue(player: Player, controlID: String) {
        if (!player.isOnline) return
        removeHighest(player, controlID)
        highestControls.values.forEach {
            it.runScript()
        }
    }

    fun removeHighest(player: Player, controlID: String) {
        highestControls.remove(controlID)
        Database.database.removeControl(player, controlID)
    }

    fun removeNormal(player: Player, controlID: String) {
        controls.remove(controlID)
        Database.database.removeControl(player, controlID)
    }

}