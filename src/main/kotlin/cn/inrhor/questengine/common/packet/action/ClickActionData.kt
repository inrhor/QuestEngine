package cn.inrhor.questengine.common.packet.action

class ClickActionData(var needClickCount: Int = 1) {

    // 记录交互次数
    var clickCountLog: Int = 0

    /**
     * 满足交互次数与否
     */
    fun passClickCount() = clickCountLog >= needClickCount

    val passOnly = mutableListOf<String>()

    val passAdd = mutableListOf<String>()

}