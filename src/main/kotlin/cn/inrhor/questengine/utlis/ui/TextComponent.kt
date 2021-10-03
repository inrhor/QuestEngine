package cn.inrhor.questengine.utlis.ui

import cn.inrhor.questengine.utlis.toJsonStr
import taboolib.module.chat.TellrawJson

/**
 * 高度自定义 JSON 内容
 *
 * 文字组件
 */
data class TextComponent(
    var text: MutableList<String> = mutableListOf(),
    var hover: MutableList<String> = mutableListOf(),
    var condition: MutableList<String> = mutableListOf(),
    var command: String = ""
) {

    fun build(): TellrawJson {
        val json = TellrawJson().append(text.toJsonStr())
        if (hover.isNotEmpty()) json.hoverText(hover.toJsonStr())
        if (command.isNotEmpty()) json.runCommand(command)
        return json
    }

    fun setCommand(type: BuilderFrame.Type, arg: String) {
        if (type == BuilderFrame.Type.SORT) {
            command = "/qen handbook sort $arg"
        }
    }

}

inline fun textComponent(component: TextComponent.() -> Unit = {}): TextComponent {
    return TextComponent().also(component)
}