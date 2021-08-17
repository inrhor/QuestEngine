package cn.inrhor.questengine.common.packet.action

class ClickAction(var needClickCount: Int) {

    // 记录交互次数
    var clickCountLog: Int = 0

    /**
     * 满足交互次数与否
     */
    fun passClickCount() = clickCountLog >= needClickCount

}