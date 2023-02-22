package cn.inrhor.questengine.common.listener.chat

import cn.inrhor.questengine.api.dialog.DialogType
import cn.inrhor.questengine.api.dialog.ReplyModule
import cn.inrhor.questengine.api.event.ReplyEvent
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.dialog.theme.chat.DialogChat
import cn.inrhor.questengine.script.kether.runEvalSet
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

object ScrollReply {

    @SubscribeEvent(EventPriority.HIGHEST, ignoreCancelled = true)
    fun scroll(ev: PlayerItemHeldEvent) {
        val p = ev.player
        val pData = p.getPlayerData()
        pData.dialogData.dialogMap.values.forEach {
            if (it.type == DialogType.CHAT) {
                val chat = it as DialogChat
                if (chat.playing) return
                val index = it.scrollIndex
                val dialog = chat.dialogModule
                val replyList = mutableListOf<ReplyModule>()
                dialog.reply.forEach { r->
                    if (runEvalSet(mutableSetOf(p), r.condition)) {
                        replyList.add(r)
                    }
                }
                val size = replyList.size
                var select: Int
                if (ev.newSlot > ev.previousSlot) {
                    select = index + 1
                    if (select >= size) {
                        select = 0
                    }
                } else {
                    select = index - 1
                    if (select < 0) {
                        select = size - 1
                    }
                }
                if (select != index) {
                    it.scrollIndex = select
                }
                return
            }
        }
    }

    // 快捷键互换主手和副手的物品时触发
    @SubscribeEvent
    fun choose(ev: PlayerSwapHandItemsEvent) {
        val p = ev.player
        val pData = p.getPlayerData()
        pData.dialogData.dialogMap.values.forEach {
            if (it.type == DialogType.CHAT) {
                val chat = it as DialogChat
                if (chat.playing) return
                val replyList = mutableListOf<ReplyModule>()
                val dialog = it.dialogModule
                dialog.reply.forEach { r->
                    if (runEvalSet(mutableSetOf(p), r.condition)) {
                        replyList.add(r)
                    }
                }
                val viewers = it.viewers
                val reply = replyList[chat.scrollIndex]
                viewers.forEach { v->
                    ReplyEvent(v, dialog, reply).call()
                }
                runEvalSet(viewers, reply.script) { s ->
                    s.rootFrame().variables()["@QenDialogID"] = dialog.dialogID
                }
                ev.isCancelled = true
                return
            }
        }
    }

}