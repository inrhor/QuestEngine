package cn.inrhor.questengine.utlis.ui

import org.bukkit.entity.Player
import taboolib.platform.compat.replacePlaceholder

/**
 * 高度自定义 JSON 内容
 *
 * 内容物组件
 */
class NoteComponent(
    var note: MutableList<String>,
    var condition: String = "",
    var fork: Boolean = false) {

    fun note(player: Player?): MutableList<String> {
        player?: return note
        return note.replacePlaceholder(player).toMutableList()
    }

    fun condition(player: Player?): String {
        player?: return condition
        return condition.replacePlaceholder(player)
    }

}