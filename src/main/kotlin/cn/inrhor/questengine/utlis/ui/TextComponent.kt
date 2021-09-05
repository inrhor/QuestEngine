package cn.inrhor.questengine.utlis.ui

import cn.inrhor.questengine.utlis.toJsonStr
import taboolib.common.platform.function.info
import taboolib.module.chat.TellrawJson

/**
 * 高度自定义 JSON 内容
 *
 * 文字组件
 */
open class TextComponent {

    var text: MutableList<String> = mutableListOf()
    var hover: MutableList<String> = mutableListOf()
    var command: String = ""

    open fun build(): TellrawJson {
        val json = TellrawJson().append(text.toJsonStr())
        if (hover.isNotEmpty()) json.hoverText(hover.toJsonStr())
        if (command.isNotEmpty()) json.runCommand(command)
        info("asdasd ${json.toRawMessage()}")
        return json
    }

}

inline fun textComponent(component: TextComponent.() -> Unit = {}): TellrawJson {
    return TextComponent().also(component).build()
}