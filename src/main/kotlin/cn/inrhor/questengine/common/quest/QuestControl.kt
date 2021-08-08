package cn.inrhor.questengine.common.quest

class QuestControl(
    val highestID: String,
    val normalID: String,
    var highestControl: MutableList<String>,
    var normalControl: MutableList<String>) {

}

enum class ControlPriority {
    HIGHEST, NORMAL
}