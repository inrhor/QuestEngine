package cn.inrhor.questengine.api.quest.control

enum class ControlPriority {
    HIGHEST, NORMAL
}

fun ControlPriority.toStr(): String {
    return when (this) {
        ControlPriority.HIGHEST -> "highest"
        else -> "normal"
    }
}

fun String.toControlPriority(): ControlPriority {
    return when (this) {
        "highest" -> ControlPriority.HIGHEST
        else -> ControlPriority.NORMAL
    }
}