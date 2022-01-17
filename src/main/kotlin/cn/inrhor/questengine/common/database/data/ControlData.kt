package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.api.quest.control.ControlPriority
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.quest.QuestControlData
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * 控制模块数据列表
 *
 * ControlID: questID-innerQuestID-priority
 *
 * @param highestControls 最高级控制，排队运行
 * @param controls 普通控制，共存运行
 */
class ControlData(var highestControls: LinkedHashMap<String, QuestControlData>,
                  var controls: MutableMap<String, QuestControlData>) {

    /**
     * /**
     * 添加最高级控制模块并进入队列等待运行
     * 添加普通控制模块并直接运行
    */
     */
    fun addControl(controlID: String, questControlData: QuestControlData) {
        when (questControlData.controlPriority) {
            ControlPriority.HIGHEST -> addHighest(controlID, questControlData)
            ControlPriority.NORMAL -> addCommon(controlID, questControlData)
        }
    }

    /**
     * 添加最高级控制模块并进入队列等待运行
     */
    fun addHighest(controlID: String, questControlData: QuestControlData) {
        highestControls[controlID] = questControlData
        if (highestControls.size < 2) {
            questControlData.runScript()
        }
    }

    /**
     * 添加普通控制模块并直接运行
     */
    fun addCommon(controlID: String, questControlData: QuestControlData) {
        controls[controlID] = questControlData
        questControlData.runScript()
    }

    /**
     * 当前最高级控制模块运行完毕后删除
     * 并进行下一个最高级控制模块
     */
    fun highestQueue(player: Player, controlID: String) {
        removeHighest(player, controlID)
        highestControls.values.forEach {
            it.runScript()
            return@forEach
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