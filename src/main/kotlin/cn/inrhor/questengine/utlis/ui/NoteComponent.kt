package cn.inrhor.questengine.utlis.ui

/**
 * 高度自定义 JSON 内容
 *
 * 内容物组件
 */
class NoteComponent(
    var note: MutableList<String>,
    var condition: MutableList<String>,
    var fork: Boolean = false) {
}