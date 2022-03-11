package cn.inrhor.questengine.common.dialog.theme.chat

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.api.dialog.theme.DialogTheme
import cn.inrhor.questengine.common.database.data.DataStorage
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.common5.util.printed

/**
 * 聊天框对话
 */
class DialogChat(override val dialogModule: DialogModule, val viewers: MutableSet<Player>, var scrollIndex: Int = 0): DialogTheme(type = Type.Chat) {

    override fun play() {
        parserContent()
        viewers.forEach {
            val pData = DataStorage.getPlayerData(it)
            pData.dialogData.addDialog(dialogModule.dialogID, this)
            pData.chatCache.open()
        }
    }

    fun parserContent() {
        val content = dialogModule.dialog
        for (i in content.indices) {
            val an = content[i].printed()
            an.forEach {
                submit(async = true, delay = 5L) {
                    viewers.forEach { p ->
                        p.sendMessage("$it§7§5§4§d§3§e§l§m§f")
                    }
                }
            }
            if (i >= content.size-1) {
                val reply = ReplyChat(this, dialogModule.reply)
                reply.play()
            }
        }
    }

    override fun end() {
        viewers.forEach {
            val pData = DataStorage.getPlayerData(it)
            pData.chatCache.close(it)
            pData.dialogData.dialogMap.remove(dialogModule.dialogID)
        }
    }

    override fun addViewer(viewer: Player) {
        viewers.add(viewer)
    }

    override fun deleteViewer(viewer: Player) {
        viewers.remove(viewer)
    }

}