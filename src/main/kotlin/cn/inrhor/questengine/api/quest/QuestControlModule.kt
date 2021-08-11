package cn.inrhor.questengine.api.quest

class QuestControlModule(
    val highestID: String,
    val normalID: String,
    var highestControl: MutableList<String>,
    var normalControl: MutableList<String>,
    var logModule: ControlLogModule) {

}

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

enum class ControlRunType {
    NORMAL, LOG
}