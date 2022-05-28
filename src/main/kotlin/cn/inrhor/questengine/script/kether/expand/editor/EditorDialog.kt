package cn.inrhor.questengine.script.kether.expand.editor

import cn.inrhor.questengine.script.kether.frameVoid
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.script
import java.util.concurrent.CompletableFuture

class EditorDialog : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val sender = (frame.script().sender as? ProxyPlayer ?: error("unknown player")).cast<Player>()
        sender.sendMessage("等待功能开放 [ Waiting... ]")
        return frameVoid()
    }
}