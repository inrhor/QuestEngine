package cn.inrhor.questengine.utlis.ui

import cn.inrhor.questengine.utlis.toJsonStr
import taboolib.module.chat.TellrawJson

/**
 * 高度自定义 JSON 内容
 *
 * 文字组件
 */
open class TextComponent {

    var text: MutableList<String> = mutableListOf()
    var hover: MutableList<String> = mutableListOf()
    var condition: MutableList<String> = mutableListOf()
    var command: String = ""

    open fun build(): TellrawJson {
        val json = TellrawJson().append(text.toJsonStr())
        if (hover.isNotEmpty()) json.hoverText(hover.toJsonStr())
        if (command.isNotEmpty()) json.runCommand(command)
        return json
    }

    open fun setCommand(type: BuilderJsonUI.Type, arg: String) {
        if (type == BuilderJsonUI.Type.SORT) {
            command = "/qen handbook sort $arg"
        }
    }

    fun copy(): TextComponent {
        val textComponent = TextComponent()
        textComponent.text = this.text
        textComponent.hover = this.hover
        textComponent.condition = this.condition
        textComponent.command = this.command
        return textComponent
    }

}

inline fun textComponent(component: TextComponent.() -> Unit = {}): TextComponent {
    return TextComponent().also(component)
}