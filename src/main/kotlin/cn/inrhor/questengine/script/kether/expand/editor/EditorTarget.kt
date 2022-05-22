package cn.inrhor.questengine.script.kether.expand.editor

import cn.inrhor.questengine.api.quest.module.inner.QuestTarget
import cn.inrhor.questengine.common.editor.EditorList.editorTargetList
import cn.inrhor.questengine.common.editor.EditorList.selectTargetList
import cn.inrhor.questengine.common.editor.EditorTarget.editorTarget
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.script
import taboolib.module.nms.inputSign
import taboolib.platform.util.asLangText
import java.util.concurrent.CompletableFuture

class EditorTarget(val ui: ActionEditor.TargetUi,
                   val questID: String = "", val innerID: String = "", val targetID: String = "",
                   val meta: String = "", val change: String = "", val page: Int = 0) : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val sender = (frame.script().sender as? ProxyPlayer ?: error("unknown player")).cast<Player>()
        when (ui) {
            ActionEditor.TargetUi.LIST -> {
                sender.editorTargetList(questID, innerID, page)
            }
            ActionEditor.TargetUi.EDIT -> {
                when (meta) {
                    else -> {
                        sender.editorTarget(questID,innerID,targetID)
                    }
                }
            }
            ActionEditor.TargetUi.ADD -> {
                sender.inputSign(arrayOf(sender.asLangText("EDITOR-PLEASE-TARGET-ID"))) {
                    val target = QuestTarget()
                    val id = it[1]
                    target.id = id
                    val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return@inputSign
                    inner.target.add(target)
                    QuestManager.saveFile(questID, innerID)
                    sender.selectTargetList(questID, innerID, id)
                }
            }
        }
        return frameVoid()
    }
}