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
    var fork: Boolean = false)