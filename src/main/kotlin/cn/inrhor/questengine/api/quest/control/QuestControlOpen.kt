package cn.inrhor.questengine.api.quest.control

/**
 * 等级性控制模块
 */
abstract class QuestControlOpen {

    abstract val controlID: String

    abstract val priority: ControlPriority

    abstract var controls: List<String>

    abstract var logOpen: ControlLogType

}