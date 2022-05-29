package cn.inrhor.questengine.script.kether.expand.editor

import cn.inrhor.questengine.script.kether.frameVoid
import cn.inrhor.questengine.common.editor.EditorList.editorFailReward
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.script.kether.player
import cn.inrhor.questengine.script.kether.selectInnerID
import cn.inrhor.questengine.script.kether.selectQuestID
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class EditorInnerFail(val ui: ActionEditor.ListUi, vararg val variable: String, val page: Int = 0) : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val sender = frame.player()
        val questID = frame.selectQuestID()
        val innerID = frame.selectInnerID()
        when (ui) {
            ActionEditor.ListUi.LIST -> {
                sender.editorFailReward(questID, innerID, page)
            }
            ActionEditor.ListUi.DEL -> {
                val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return frameVoid()
                val list = inner.reward.fail.toMutableList()
                list.removeAt(variable[0].toInt())
                inner.reward.fail = list
                QuestManager.saveFile(questID, innerID)
                sender.editorFailReward(questID, innerID)
            }
        }
        return frameVoid()
    }
}