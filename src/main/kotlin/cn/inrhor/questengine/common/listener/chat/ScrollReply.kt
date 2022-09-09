package cn.inrhor.questengine.common.listener.chat

import cn.inrhor.questengine.api.dialog.theme.DialogTheme
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.dialog.DialogManager.setId
import cn.inrhor.questengine.common.dialog.theme.chat.DialogChat
import cn.inrhor.questengine.script.kether.runEvalSet
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer

object ScrollReply {

    @SubscribeEvent(EventPriority.HIGHEST, ignoreCancelled = true)
    fun scroll(ev: PlayerItemHeldEvent) {
        val p = ev.player
        val pData = p.getPlayerData()
        pData.dialogData.dialogMap.values.forEach {
            if (it.type == DialogTheme.Type.Chat) {
                val chat = it as DialogChat
                if (chat.playing) return
                val index = it.scrollIndex
                val size = chat.dialogModule.reply.size
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
                    it.json.setId().sendTo(adaptPlayer(p))
                    it.replyChat.sendReply(mutableSetOf(p))
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
            if (it.type == DialogTheme.Type.Chat) {
                val chat = it as DialogChat
                val reply = it.dialogModule.reply[chat.scrollIndex]
                runEvalSet(it.viewers, reply.script)
                it.end()
                ev.isCancelled = true
                return
            }
        }
    }

}