package cn.inrhor.questengine.common.dialog.theme.chat

import cn.inrhor.questengine.api.dialog.ReplyModule
import cn.inrhor.questengine.api.dialog.theme.ReplyTheme
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.dialog.DialogManager.sendBarHelp
import cn.inrhor.questengine.common.dialog.FlagDialog
import cn.inrhor.questengine.common.dialog.hasFlag
import cn.inrhor.questengine.script.kether.runEvalSet
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.submit
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored

class ReplyChat(val dialogChat: DialogChat, val reply: List<ReplyModule>) : ReplyTheme {

    /**
     * 已解析的回复选择Json
     */
    var replyJson = mutableListOf<TellrawJson>()

    /**
     * 执行对话flag
     */
    fun executeFlag() {
        val flag = dialogChat.dialogModule.flag
        if (flag.hasFlag(FlagDialog.KEEP_CONTENT)) { // 保持对话，一直显示
            submit(async = true, period = 5L) {
                val viewers = dialogChat.viewers
                if (viewers.isEmpty()) {
                    cancel()
                    return@submit
                }
                sendReply()
            }
        }
    }

    fun sendReply() {
        dialogChat.viewers.forEach {
            val reply = replyJson[dialogChat.scrollIndex]
            reply.sendTo(adaptPlayer(it))
        }
    }

    override fun play() {
        val dialog = dialogChat.dialogModule
        dialogChat.viewers.forEach {
            val pData = it.getPlayerData()
            pData.dialogData.addReply(dialog.dialogID, this)
        }
        parserReply()
        sendReply()
        sendBarHelp(dialogChat)
        executeFlag()
    }

    /**
     * 解析回复选择到缓存中，包括对话主体内容
     */
    fun parserReply() {
        replyJson = mutableListOf()
        val replyList = mutableListOf<ReplyModule>()
        reply.forEach {
            if (runEvalSet(dialogChat.viewers, it.condition)) {
                replyList.add(it)
                replyJson.add(TellrawJson().append(dialogChat.json))
            }
        }
        val replySize = replyList.size
        for (i in 0 until replySize) { // 遍历回复
            for (a in 0 until replySize) { // 再次遍历回复，为了添加前缀
                val r = replyList[a]
                val px = if (i == a) r.tagChoose.ifEmpty { dialogChat.dialogModule.replyChoose } else r.tagDefault.ifEmpty { dialogChat.dialogModule.replyDefault }
                r.content.forEach {
                    replyJson[i].append((px + it).colored()).newLine()
                }
            }
        }
    }

    override fun end() {
        // 暂无可操作
    }

}