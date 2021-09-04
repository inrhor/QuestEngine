package cn.inrhor.questengine.utlis.ui

/**
 * 高度自定义 JSON 内容
 *
 * 文字组件
 */
open class TextComponent {

    var text: MutableList<String> = mutableListOf()
    var hover: MutableList<String> = mutableListOf()
    var command: String = ""

}

inline fun textComponent(component: TextComponent.() -> Unit = {}): TextComponent {
    return TextComponent().also(component)
}