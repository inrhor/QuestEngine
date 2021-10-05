package cn.inrhor.questengine.utlis.ui

import cn.inrhor.questengine.utlis.toJsonStr
import org.bukkit.entity.Player
import taboolib.module.chat.TellrawJson
import taboolib.platform.compat.replacePlaceholder

/**
 * 高度自定义 JSON 内容
 *
 * 文字组件
 */
data class TextComponent(
    var text: MutableList<String> = mutableListOf(),
    var hover: MutableList<String> = mutableListOf(),
    var condition: MutableList<String> = mutableListOf(),
    var command: String = "",
    var type: BuilderFrame.Type = BuilderFrame.Type.CUSTOM
) {

    fun build(): TellrawJson {
        val json = TellrawJson().append(text.toJsonStr())
        if (hover.isNotEmpty()) json.hoverText(hover.toJsonStr())
        if (command.isNotEmpty()) json.runCommand(command)
        return json
    }

    fun autoCommand(arg: String) {
        if (type == BuilderFrame.Type.SORT) {
            command = "/qen handbook sort $arg"
        }
    }

    fun text(player: Player): List<String> {
        return text.replacePlaceholder(player)
    }

}

inline fun textComponent(component: TextComponent.() -> Unit = {}): TextComponent {
    return TextComponent().also(component)
}