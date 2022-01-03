package cn.inrhor.questengine.api.quest.control

/**
 * 高级控制模块
 */
class ControlHighestModule(
    override val controlID: String,
    override var controls: List<String>,
    override var logOpen: ControlLogType
): QuestControlOpen() {

    override val priority: ControlPriority = ControlPriority.HIGHEST

}
