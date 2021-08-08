package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.common.database.data.quest.QuestControlData

/**
 * ControlID: [questID]-[innerQuestID]-[priority]
 *
 * @param highestControls 最高级控制，排队运行
 * @param controls 普通控制，共存运行
 */
class ControlData(var highestControls: LinkedHashMap<String, QuestControlData>,
                  var controls: MutableMap<String, QuestControlData>) {

    /**
     * 添加最高级控制模块并进入队列等待运行
     * 添加普通控制模块并直接运行
     */
    fun addControl(highestID: String, normalID: String, highestControlData: QuestControlData, normalControlData: QuestControlData) {
        addHighest(highestID, highestControlData)
        addCommon(normalID, normalControlData)
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
    fun highestQueue(controlID: String) {
        removeHighest(controlID)
        highestControls.values.forEach {
            it.runScript()
            return@forEach
        }
    }

    fun removeHighest(controlID: String) {
        highestControls.remove(controlID)
    }

    fun removeNormal(controlID: String) {
        controls.remove(controlID)
    }

}