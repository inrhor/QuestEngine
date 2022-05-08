package cn.inrhor.questengine.api.quest.module.inner

import cn.inrhor.questengine.api.quest.control.ControlPriority

class QuestControl(val id: String, val level: ControlPriority, val log: ControlLog, val script: MutableList<String>) {

    constructor(): this("null", ControlPriority.NORMAL, ControlLog(), mutableListOf())

}

class ControlLog(val enable: Boolean, val type: String, val recall: MutableList<String>) {
    constructor(): this(false, "normal", mutableListOf())

    fun returnReCall(questID: String, innerID: String, priority: String): List<String> {
        return replaceList(recall.toMutableList(), questID, innerID, priority)
    }

    private fun replaceList(list: MutableList<String>, questID: String, innerID: String, priority: String): MutableList<String> {
        list.forEach {
            list.add(it.replace("@this", "$questID $innerID $priority", true))
        }
        return list
    }
}