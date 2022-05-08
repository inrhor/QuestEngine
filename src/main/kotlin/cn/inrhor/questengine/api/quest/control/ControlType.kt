package cn.inrhor.questengine.api.quest.control

enum class ControlPriority {
    HIGHEST, NORMAL
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