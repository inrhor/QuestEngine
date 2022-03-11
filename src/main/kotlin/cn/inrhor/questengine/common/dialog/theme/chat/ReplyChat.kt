package cn.inrhor.questengine.common.dialog.theme.chat

import cn.inrhor.questengine.api.dialog.ReplyModule
import cn.inrhor.questengine.api.dialog.theme.ReplyTheme
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.script.kether.runEvalSet

class ReplyChat(val dialogChat: DialogChat, val reply: List<ReplyModule>): ReplyTheme {

    override fun play() {
        dialogChat.viewers.forEach {
            val pData = DataStorage.getPlayerData(it)
            pData.dialogData.addReply(dialogChat.dialogModule.dialogID, this)
        }
        reply.forEach {
            if (runEvalSet(dialogChat.viewers, it.condition)) {
                handleContent(it)
            }
        }
    }

    private fun handleContent(replyModule: ReplyModule) {
        dialogChat.viewers.forEach {
            val list = replyModule.content
            for (i in 0 until list.size) {
                if (i == dialogChat.scrollIndex) {
                    it.sendMessage(replyModule.tagChoose+list[i]+"§7§5§4§d§3§e§l§m§f")
                }else it.sendMessage(replyModule.tagDefault+list[i]+"§7§5§4§d§3§e§l§m§f")
            }
        }
    }

    override fun end() {
        // 暂无可操作
    }

}