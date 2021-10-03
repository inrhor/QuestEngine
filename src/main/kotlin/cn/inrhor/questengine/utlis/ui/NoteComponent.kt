package cn.inrhor.questengine.utlis.ui

/**
 * 高度自定义 JSON 内容
 *
 * 内容物组件
 */
class NoteComponent(
    val note: MutableList<String> = mutableListOf(),
    var fork: Boolean = false) {
}