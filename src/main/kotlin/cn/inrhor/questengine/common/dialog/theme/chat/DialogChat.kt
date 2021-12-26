package cn.inrhor.questengine.common.dialog.theme.chat

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.api.dialog.theme.DialogTheme
import org.bukkit.entity.Player

/**
 * 聊天框对话
 */
class DialogChat(
    override val dialogModule: DialogModule): DialogTheme() {

    override fun play() {

    }

    override fun end() {

    }

    override fun addViewer(viewer: Player) {

    }

    override fun deleteViewer(viewer: Player) {

    }

}