package cn.inrhor.questengine.common.dialog.theme.chat

import cn.inrhor.questengine.api.dialog.ReplyModule
import cn.inrhor.questengine.api.dialog.theme.ReplyTheme
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.script.kether.runEvalSet
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.TellrawJson

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
                val text = if (i == dialogChat.scrollIndex) {
                    replyModule.tagChoose+list[i]
                }else replyModule.tagDefault+list[i]
                TellrawJson()
                    .append(text).insertion("@d31877bc-b8bc-4355-a4e5-9b055a494e9f")
                    .sendTo(adaptPlayer(it))
            }
        }
    }

    override fun end() {
        // 暂无可操作
    }

}