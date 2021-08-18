package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.api.packet.*
import cn.inrhor.questengine.common.packet.PacketManager
import cn.inrhor.questengine.script.kether.evalBoolean
import taboolib.common.platform.ProxyPlayer
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class KetherAddClick(val type: String, val add: Int, val shell: ParsedAction<*>, val entityID: Int): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        return frame.newFrame(shell).run<String>().thenAccept {
            val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
            val packetData = PacketManager.getPacketData(player.cast(), entityID)?: return@thenAccept
            if (evalBoolean(player.cast(), it)) packetData.clickAction.clickCountLog += add
        }
    }

    internal object Parser {
        @KetherParser(["addClickCount"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            it.mark()
            val type = it.nextToken()
            val add = it.nextInt()
            it.mark()
            it.expect("is")
            val shell = it.next(ArgTypes.ACTION)
            it.mark()
            it.expect("to")
            KetherAddClick(type, add, shell, it.nextInt())
        }
    }

}