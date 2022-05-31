package cn.inrhor.questengine.script.kether.expand.editor

import cn.inrhor.questengine.api.quest.module.inner.FinishReward
import cn.inrhor.questengine.common.editor.EditorList.editorFinishReward
import cn.inrhor.questengine.common.editor.EditorList.editorRewardList
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.script.kether.*
import cn.inrhor.questengine.utlis.newLineList
import taboolib.common.util.addSafely
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.nms.inputSign
import taboolib.platform.util.asLangText
import java.util.concurrent.CompletableFuture

class EditorReward(val ui: ActionEditor.RewardUi, vararg var variable: String, val page: Int = 0) : ScriptAction<Void>() {
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
                inner.reward.finish.removeAt(variable[0].toInt())
                QuestManager.saveFile(questID, innerID)
                sender.editorFinishReward(questID, innerID, frame.selectRewardID())
            }
            ActionEditor.RewardUi.ADD -> {
                sender.inputSign(arrayOf(sender.asLangText("EDITOR-PLEASE-EVAL"))) {
                    val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return@inputSign
                    val reward = inner.reward.getFinishReward(frame.selectRewardID())?: return@inputSign
                    val list = reward.script.newLineList()
                    val index = if (variable[0]=="{head}") 0 else variable[0].toInt()+1
                    list.addSafely(index, it[1], "")
                    reward.script = list.joinToString("\n")
                    QuestManager.saveFile(questID, innerID)
                    sender.editorFinishReward(questID, innerID, frame.selectRewardID())
                }
            }
            ActionEditor.RewardUi.CREATE -> {
                sender.inputSign(arrayOf(sender.asLangText("EDITOR-PLEASE-REWARD-ID"))) {
                    val id = it[1]
                    if (id.isEmpty()) return@inputSign
                    val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return@inputSign
                    if (!inner.reward.existRewardID(id)) {
                        inner.reward.finish.add(FinishReward(id, ""))
                    }
                    QuestManager.saveFile(questID, innerID)
                    sender.editorFinishReward(questID, innerID, id)
                }
            }
        }
        return frameVoid()
    }
}