package cn.inrhor.questengine.api.quest.control

/**
 * 等级性控制模块
 */
abstract class QuestControlOpen {

    abstract val controlID: String

    abstract val priority: ControlPriority

    abstract var controls: MutableList<String>

    abstract var logOpen: ControlLogType

}