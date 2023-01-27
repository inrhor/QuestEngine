package cn.inrhor.questengine.common.dialog.theme.chat

import cn.inrhor.questengine.api.dialog.ReplyModule
import cn.inrhor.questengine.api.dialog.theme.ReplyTheme
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.dialog.DialogManager
import cn.inrhor.questengine.common.dialog.DialogManager.setId
import cn.inrhor.questengine.script.kether.runEvalSet
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored
import taboolib.platform.compat.replacePlaceholder

class ReplyChat(val dialogChat: DialogChat, val reply: List<ReplyModule>): ReplyTheme {

    override fun play() {
        val dialog = dialogChat.dialogModule
        dialogChat.viewers.forEach {
            val pData = it.getPlayerData()
            pData.dialogData.addReply(dialog.dialogID, this)
        }
        sendReply(dialogChat.viewers)
        DialogManager.sendBarHelp(dialogChat)
    }

    fun sendReply(viewers: MutableSet<Player>, replyList: MutableList<ReplyModule> = mutableListOf()) {
        if (replyList.isEmpty()) {
            reply.forEach {
                if (runEvalSet(viewers, it.condition)) {
                    replyList.add(it)
                }
            }
        }
        for ((has, i) in replyList.indices.withIndex()) {
            val r = replyList[i]
            val isEnd = i >= replyList.size-1
            if (has == dialogChat.scrollIndex) {
                val choose = r.tagChoose.ifEmpty { dialogChat.dialogModule.replyChoose }
                handleContent(viewers, r, choose, isEnd)
            }else {
                val def = r.tagDefault.ifEmpty { dialogChat.dialogModule.replyDefault }
                handleContent(viewers, r, def, isEnd)
            }
        }
    }

    private fun handleContent(viewers: MutableSet<Player>, replyModule: ReplyModule, prefix: String, isEnd: Boolean) {
        viewers.forEach {
            val list = replyModule.content
            val json = TellrawJson()
            for (i in 0 until list.size) {
                json.append((prefix+list[i]).replacePlaceholder(it).colored())
            }
            if (isEnd) json.newLine()
            json.setId().sendTo(adaptPlayer(it))
        }
    }

    override fun end() {
        // 暂无可操作
    }

}