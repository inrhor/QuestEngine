package cn.inrhor.questengine.script.kether.expand.editor

import cn.inrhor.questengine.common.editor.EditorList.editorFinishReward
import cn.inrhor.questengine.common.editor.EditorList.editorRewardList
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.script.kether.*
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class EditorReward(val ui: ActionEditor.RewardUi, val index: Int = 0, val page: Int = 0) : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val sender = frame.player()
        val questID = frame.selectQuestID()
        val innerID = frame.selectInnerID()
        when (ui) {
            ActionEditor.RewardUi.LIST -> {
                sender.editorRewardList(questID, innerID, page)
            }
            ActionEditor.RewardUi.EDIT -> {
                sender.editorFinishReward(questID, innerID, frame.selectRewardID(), page)
            }
            ActionEditor.RewardUi.DEL -> {
                val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return frameVoid()
                inner.reward.finish.removeAt(index)
                QuestManager.saveFile(questID, innerID)
                sender.editorFinishReward(questID, innerID, frame.selectRewardID())
            }
        }
        return frameVoid()
    }
}