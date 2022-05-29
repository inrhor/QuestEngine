package cn.inrhor.questengine.api.quest.module.inner

import cn.inrhor.questengine.api.quest.control.ControlPriority

class QuestControl(val id: String, val level: ControlPriority, val log: ControlLog, private val script: String) {

    constructor(): this("null", ControlPriority.NORMAL, ControlLog(), "")

    fun control(questID: String, innerID: String) = log.replaceVar(script, questID, innerID, id)

}

class ControlLog(val enable: Boolean, val type: String, val recall: String) {
    constructor(): this(false, "normal", "")

    fun replaceRecall(questID: String, innerID: String, id: String): String {
        return replaceVar(recall, questID, innerID, id)
    }

    fun replaceVar(eval : String, questID: String, innerID: String, id: String): String {
        return eval.replace("@this", "quest select $questID inner select $innerID control select $id")
    }
}