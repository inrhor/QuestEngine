package cn.inrhor.questengine.api.quest.module.inner

class TimeModule(var type: Type, var duration: String) {

    constructor():this(Type.ALWAYS, "")

    enum class Type {
        ALWAYS, WEEKLY, MONTHLY, YEARLY, CUSTOM
    }

    fun lang(): String {
        return ""
    }

}