package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.api.packet.*
import cn.inrhor.questengine.common.packet.PacketManager
import cn.inrhor.questengine.script.kether.runEval

import taboolib.common.platform.ProxyPlayer
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class KetherAddClick(val type: String, val add: Int, val shell: ParsedAction<*>, val entityID: Int): ScriptAction<Boolean>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
        val isBol = CompletableFuture<Boolean>()
        frame.newFrame(shell).run<String>().thenAccept {
            val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
            try {
                if (runEval(player.cast(), it)) {
                    val packetData = PacketManager.getPacketData(player.cast(), entityID)?: return@thenAccept
                    val clickAction = packetData.clickAction
                    val need = clickAction.needClickCount
                    val log = clickAction.clickCountLog
                    if (need < log + add) packetData.clickAction.clickCountLog = need else packetData.clickAction.clickCountLog += add
                    isBol.complete(true)
                }
            }catch (ex: Exception) {
            }
        }
        return isBol
    }

    internal object Parser {
        @KetherParser(["addClickCount"], namespace = "QuestEngine")
        fun parser() = scriptParser {
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