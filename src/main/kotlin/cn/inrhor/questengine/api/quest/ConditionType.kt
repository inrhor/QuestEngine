package cn.inrhor.questengine.api.quest

open class ConditionType(val content: String) {

    open fun check(): Boolean {
        return false
    }

}