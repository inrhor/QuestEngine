package cn.inrhor.questengine.common.quest

import java.util.*

class QuestControl(val controlID: String, var priority: ControlPriority, var scriptList: MutableList<String>) {

}

enum class ControlPriority {
    HIGHEST, NORMAL
}

fun String.toControlPriority(): ControlPriority {
    return when (this.uppercase(Locale.getDefault())) {
        "HIGHEST" -> ControlPriority.HIGHEST
        else -> ControlPriority.NORMAL
    }
}