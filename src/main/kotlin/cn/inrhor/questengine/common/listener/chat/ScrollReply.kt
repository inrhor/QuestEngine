package cn.inrhor.questengine.common.listener.chat

import cn.inrhor.questengine.api.dialog.DialogType
import cn.inrhor.questengine.api.dialog.ReplyModule
import cn.inrhor.questengine.api.event.ReplyEvent
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.dialog.FlagDialog
import cn.inrhor.questengine.common.dialog.hasFlag
import cn.inrhor.questengine.common.dialog.theme.chat.DialogChat
import cn.inrhor.questengine.script.kether.runEvalSet
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.Baffle
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.PacketReceiveEvent
import java.util.concurrent.TimeUnit

object ScrollReply {

    @SubscribeEvent(EventPriority.HIGHEST, ignoreCancelled = true)
    fun scroll(ev: PlayerItemHeldEvent) {
        val p = ev.player
        val pData = p.getPlayerData()
        pData.dialogData.dialogMap.values.forEach {
            if (it.type == DialogType.CHAT) {
                val chat = it as DialogChat
                if (chat.playing) return
                if (it.dialogModule.flag.hasFlag(FlagDialog.WS)) return
                val w = ev.newSlot > ev.previousSlot
                scrollReply(chat, p, w, true)
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
                if (it.dialogModule.flag.hasFlag(FlagDialog.AD_CHOOSE)) return
                chooseReply(chat, p, ev)
            }
        }
    }

    fun chooseReply(it: DialogChat, p: Player, ev: Cancellable) {
        val replyList = mutableListOf<ReplyModule>()
        val dialog = it.dialogModule
        dialog.reply.forEach { r->
            if (runEvalSet(mutableSetOf(p), r.condition)) {
                replyList.add(r)
            }
        }
        val viewers = it.viewers
        val reply = replyList[it.scrollIndex]
        viewers.forEach { v ->
            ReplyEvent(v, dialog, reply).call()
        }
        runEvalSet(viewers, reply.script) { s ->
            s.rootFrame().variables()["@QenDialogID"] = dialog.dialogID
        }
        ev.isCancelled = true
    }

    /**
     * 阻断器监听WASD
     */
    val baffle1 = Baffle.of(500, TimeUnit.MILLISECONDS)

    @SubscribeEvent
    fun ws(ev: PacketReceiveEvent) {
        if (ev.isCancelled) return
        if (ev.packet.name != "PacketPlayInSteerVehicle") return
        val p = ev.player
        val pData = p.getPlayerData()
        pData.dialogData.dialogMap.values.forEach {
            if (it.type == DialogType.CHAT) {
                val chat = it as DialogChat
                if (chat.playing) return
                val list = if (MinecraftVersion.isUniversal) listOf("c", "d", "e") else listOf("a", "b", "c")
                if (it.dialogModule.flag.hasFlag(FlagDialog.WS)) {
                    if (baffle1.hasNext(p.name)) {
                        val swSpeed = ev.packet.read<Float>(list[1])?: return // 前进速度
                        scrollReply(chat, p, swSpeed > 0.0, swSpeed < 0.0)
                    }
                }
                if (it.dialogModule.flag.hasFlag(FlagDialog.AD_CHOOSE)) {
                    val adSpeed = ev.packet.read<Float>(list[0])?: return
                    if (adSpeed != 0f) {
                        chooseReply(chat, p, ev)
                    }
                }
                return
            }
        }
    }

    private fun scrollReply(chat: DialogChat, p: Player, w: Boolean, s: Boolean) {
        val replyList = mutableListOf<ReplyModule>()
        val dialog = chat.dialogModule
        dialog.reply.forEach { r->
            if (runEvalSet(mutableSetOf(p), r.condition)) {
                replyList.add(r)
            }
        }
        val index = chat.scrollIndex
        val size = replyList.size
        var select: Int? = null
        if (w) {
            select = index + 1
            if (select >= size) {
                select = 0
            }
        }else if (s) {
            select = index - 1
            if (select < 0) {
                select = size - 1
            }
        }
        if (select != null && select != index) {
            chat.scrollIndex = select
        }
        if (!dialog.flag.hasFlag(FlagDialog.KEEP_CONTENT)) chat.replyChat.sendReply()
    }

}