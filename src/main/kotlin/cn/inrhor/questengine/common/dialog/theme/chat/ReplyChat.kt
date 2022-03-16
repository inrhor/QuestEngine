package cn.inrhor.questengine.common.dialog.theme.chat

import cn.inrhor.questengine.api.dialog.ReplyModule
import cn.inrhor.questengine.api.dialog.theme.ReplyTheme
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.dialog.DialogManager.setId
import cn.inrhor.questengine.script.kether.runEvalSet
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.info
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored
import taboolib.platform.compat.replacePlaceholder
import taboolib.platform.util.asLangText

class ReplyChat(val dialogChat: DialogChat, val reply: List<ReplyModule>): ReplyTheme {

    override fun play() {
        dialogChat.viewers.forEach {
            val pData = DataStorage.getPlayerData(it)
            pData.dialogData.addReply(dialogChat.dialogModule.dialogID, this)
            adaptPlayer(it).sendActionBar(it.asLangText("DIALOG-CHAT-HELP"))
        }
        sendReply(dialogChat.viewers)
    }

    fun sendReply(viewers: MutableSet<Player>) {
        var has = 0
        for (i in reply.indices) {
            val r = reply[i]
            if (runEvalSet(viewers, r.condition)) {
                if (has == dialogChat.scrollIndex) {
                    handleContent(viewers, r, r.tagChoose)
                }else handleContent(viewers, r, r.tagDefault); has++
            }else has ++
        }
    }

    private fun handleContent(viewers: MutableSet<Player>, replyModule: ReplyModule, prefix: String) {
        viewers.forEach {
            val list = replyModule.content
            val json = TellrawJson()
            for (i in 0 until list.size) {
                json.append((prefix+list[i]).replacePlaceholder(it).colored())
            }
            json.setId().sendTo(adaptPlayer(it))
        }
    }

    override fun end() {
        // 暂无可操作
    }

}