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
class DialogChat(override val dialogModule: DialogModule, val viewers: MutableSet<Player>): DialogTheme(type = Type.Chat) {

    override fun play() {
        parserContent()
        viewers.forEach {
            val pData = DataStorage.getPlayerData(it)
            pData.dialogData.addDialog(dialogModule.dialogID, this)
            pData.chatCache.open()
        }
    }

    fun parserContent() {
        dialogModule.dialog.forEach {
            val an = it.printed()
            an.forEach { i ->
                submit(async = true, delay = 5L) {
                    viewers.forEach { p ->
                        p.sendMessage(i)
                    }
                }
            }
        }
    }

    override fun end() {
        viewers.forEach {
            val pData = DataStorage.getPlayerData(it)
            pData.chatCache.close(it)
        }
    }

    override fun addViewer(viewer: Player) {
        viewers.add(viewer)
    }

    override fun deleteViewer(viewer: Player) {
        viewers.remove(viewer)
    }

}