package cn.inrhor.questengine.script.kether.expand.editor

import cn.inrhor.questengine.common.edit.EditorList.editorTargetList
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.script
import java.util.concurrent.CompletableFuture

class EditorTarget(val ui: ActionEditor.TargetUi, val questID: String = "", val innerID: String = "", val meta: String = "", val change: String = "", val page: Int = 0) : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val sender = (frame.script().sender as? ProxyPlayer ?: error("unknown player")).cast<Player>()
        when (ui) {
            ActionEditor.TargetUi.LIST -> {
                sender.editorTargetList(questID, innerID, page)
            }
        }
        return frameVoid()
    }
}