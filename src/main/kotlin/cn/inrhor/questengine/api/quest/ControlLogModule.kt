package cn.inrhor.questengine.api.quest

class ControlLogModule(
    var highestLogEnable: Boolean,
    var normalLogEnable: Boolean,
    var highestLogType: String,
    var normalLogType: String,
    var highestReKether: MutableList<String>,
    var normalReKether: MutableList<String>) {

}