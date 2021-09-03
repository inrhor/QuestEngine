package cn.inrhor.questengine.utlis.ui

/**
 * 高度自定义 JSON 内容
 *
 * 窗口组件
 */
open class BuilderUI {

    val description = mutableListOf<String>()
    val textList = mutableMapOf<String, TextComponent>()

}

inline fun buildUI(builder: BuilderUI.() -> Unit = {}): BuilderUI {
    return BuilderUI().also(builder)
}