package cn.inrhor.questengine.api.quest.module.inner

import cn.inrhor.questengine.api.quest.control.ControlPriority

class QuestControl(val id: String, val level: ControlPriority, val log: ControlLog, private val script: MutableList<String>) {

    constructor(): this("null", ControlPriority.NORMAL, ControlLog(), mutableListOf())

    fun control(questID: String, innerID: String) = log.replaceList(script, questID, innerID, id)

}

class ControlLog(val enable: Boolean, val type: String, val recall: MutableList<String>) {
    constructor(): this(false, "normal", mutableListOf())

    fun returnReCall(questID: String, innerID: String, id: String): List<String> {
        return replaceList(recall, questID, innerID, id)
    }

    fun replaceList(list: MutableList<String>, questID: String, innerID: String, id: String): MutableList<String> {
        val l = mutableListOf<String>()
        list.forEach {
            if (it.uppercase().startsWith("WAITTIME ")) {
                l.add("$it to $questID $innerID $id")
            }else l.add(it.replace("@this", "$questID $innerID"))
        }
        return l
    }
}