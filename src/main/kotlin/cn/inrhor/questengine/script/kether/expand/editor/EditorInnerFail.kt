package cn.inrhor.questengine.script.kether.expand.editor

import cn.inrhor.questengine.script.kether.frameVoid
import cn.inrhor.questengine.common.editor.EditorList.editorFailReward
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.script
import java.util.concurrent.CompletableFuture

class EditorInnerFail(val ui: ActionEditor.ListUi, val questID: String = "", val innerID: String = "", val meta: String = "", val change: String = "", val page: Int = 0) : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val sender = (frame.script().sender as? ProxyPlayer ?: error("unknown player")).cast<Player>()
        when (ui) {
            ActionEditor.ListUi.LIST -> {
                sender.editorFailReward(questID, innerID, page)
            }
            ActionEditor.ListUi.DEL -> {
                val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return frameVoid()
                val list = inner.reward.fail.toMutableList()
                list.removeAt(change.toInt())
                inner.reward.fail = list
                QuestManager.saveFile(questID, innerID)
                sender.editorFailReward(questID, innerID)
            }
        }
        return frameVoid()
    }
}