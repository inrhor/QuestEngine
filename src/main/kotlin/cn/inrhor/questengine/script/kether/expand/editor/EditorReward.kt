package cn.inrhor.questengine.script.kether.expand.editor

import cn.inrhor.questengine.common.edit.EditorList.editorFinishReward
import cn.inrhor.questengine.common.edit.EditorList.editorRewardList
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.script
import java.util.concurrent.CompletableFuture

class EditorReward(val ui: ActionEditor.RewardUi,
                   val questID: String = "", val innerID: String = "", val rewardID: String = "",
                   val meta: String = "", val index: Int = 0, val page: Int = 0) : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val sender = (frame.script().sender as? ProxyPlayer ?: error("unknown player")).cast<Player>()
        when (ui) {
            ActionEditor.RewardUi.LIST -> {
                sender.editorRewardList(questID, innerID, page)
            }
            ActionEditor.RewardUi.EDIT -> {
                sender.editorFinishReward(questID, innerID, rewardID, page)
            }
            ActionEditor.RewardUi.DEL -> {
                val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return frameVoid()
                inner.reward.finish.removeAt(index)
                QuestManager.saveFile(questID, innerID)
                sender.editorFinishReward(questID, innerID, rewardID)
            }
        }
        return frameVoid()
    }
}