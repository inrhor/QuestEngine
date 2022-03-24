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

fun ControlPriority.toInt(): Int {
    return when (this) {
        ControlPriority.HIGHEST -> 1
        else -> 0
    }
}

fun Int.toControlPriority(): ControlPriority {
    return when (this) {
        1 -> ControlPriority.HIGHEST
        else -> ControlPriority.NORMAL
    }
}

fun String.toControlPriority(): ControlPriority {
    return when (this) {
        "highest" -> ControlPriority.HIGHEST
        else -> ControlPriority.NORMAL
    }
}