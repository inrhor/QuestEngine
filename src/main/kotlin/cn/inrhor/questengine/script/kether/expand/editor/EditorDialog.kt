package cn.inrhor.questengine.script.kether.expand.editor

import cn.inrhor.questengine.script.kether.frameVoid
import cn.inrhor.questengine.script.kether.player
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class EditorDialog : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val sender = frame.player()
        sender.sendMessage("等待功能开放 [ Waiting... ]")
        return frameVoid()
    }
}