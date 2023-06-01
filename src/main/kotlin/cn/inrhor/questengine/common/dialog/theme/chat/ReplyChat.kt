package cn.inrhor.questengine.common.dialog.theme.chat

import cn.inrhor.questengine.api.dialog.ReplyModule
import cn.inrhor.questengine.api.dialog.theme.ReplyTheme
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.dialog.DialogManager.copy
import cn.inrhor.questengine.common.dialog.DialogManager.sendBarHelp
import cn.inrhor.questengine.common.dialog.FlagDialog
import cn.inrhor.questengine.common.dialog.hasFlag
import cn.inrhor.questengine.script.kether.evalString
import cn.inrhor.questengine.script.kether.runEvalSet
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.submit
import taboolib.module.chat.ComponentText
import taboolib.module.chat.colored
import taboolib.platform.compat.replacePlaceholder

class ReplyChat(val dialogChat: DialogChat, val reply: List<ReplyModule>) : ReplyTheme {

    /**
     * 已解析的回复选择Json
     */
    var replyComponent = mutableListOf<ComponentText>()

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
            val reply = replyComponent[dialogChat.scrollIndex]
            val replyContext = ComponentText.raw(it.evalString(
                reply.toRawMessage().replacePlaceholder(it), "{{", "}}") {})
            replyContext.sendTo(adaptPlayer(it))
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
        replyComponent = mutableListOf()
        val replyList = mutableListOf<ReplyModule>()
        reply.forEach {
            if (runEvalSet(dialogChat.viewers, it.condition)) {
                replyList.add(it)
                replyComponent.add(dialogChat.componentText.copy())
            }
        }
        replyList.forEach {// 遍历回复
            val index = replyList.indexOf(it)
            replyList.forEach { a -> // 再次遍历回复，为了添加前缀
                val pxIndex = replyList.indexOf(a)
                val px = if (index == pxIndex) a.tagChoose.ifEmpty { dialogChat.dialogModule.replyChoose } else a.tagDefault
                    .ifEmpty { dialogChat.dialogModule.replyDefault }
                a.content.forEach { c ->
                        replyComponent[index].append((px + c).colored()).newLine()
                }
            }
        }
    }

    override fun end() {
        // 暂无可操作
    }

}