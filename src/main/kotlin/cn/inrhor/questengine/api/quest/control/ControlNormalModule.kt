package cn.inrhor.questengine.api.quest.control

/**
 * 高级控制模块
 */
class ControlNormalModule(
    override val controlID: String,
    override var controls: MutableList<String>,
    override var logOpen: ControlLogType
): QuestControlOpen() {

    override val priority: ControlPriority = ControlPriority.NORMAL

}
