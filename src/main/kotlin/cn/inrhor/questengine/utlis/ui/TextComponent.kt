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

    fun build(player: Player?): TellrawJson {
        val json = TellrawJson().append(text(player).toJsonStr())
        if (hover.isNotEmpty()) json.hoverText(hover(player).toJsonStr())
        if (command.isNotEmpty()) json.runCommand(command(player))
        return json
    }

    fun autoCommand(arg: String) {
        if (type == BuilderFrame.Type.SORT) {
            command = "/qen handbook sort $arg"
        }
    }

    fun text(player: Player?): MutableList<String> {
        player?: return text
        return text.replacePlaceholder(player).toMutableList()
    }

    fun hover(player: Player?): MutableList<String> {
        player?: return hover
        return hover.replacePlaceholder(player).toMutableList()
    }

    fun condition(player: Player?): MutableList<String> {
        player?: return condition
        return condition.replacePlaceholder(player).toMutableList()
    }

    fun command(player: Player?): String {
        player?: return command
        return command.replacePlaceholder(player).replace("{player}", player.name)
    }

}

inline fun textComponent(component: TextComponent.() -> Unit = {}): TextComponent {
    return TextComponent().also(component)
}