package cn.inrhor.questengine.common.listener.quest

import cn.inrhor.questengine.api.event.TargetEvent
import cn.inrhor.questengine.api.manager.DataManager.completedTargets
import cn.inrhor.questengine.api.manager.DataManager.targetData
import cn.inrhor.questengine.api.manager.DataManager.teamData
import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.api.quest.QueueType
import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.common.quest.enum.ModeType
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.quest.manager.QuestManager.finishQuest
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestFrame
import cn.inrhor.questengine.script.kether.runEvalSet
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent

object TargetListener {

    @SubscribeEvent
    fun finish(ev: TargetEvent.Finish) {
        val p = ev.player
        val t = ev.targetData
        t.state = StateType.FINISH
        val questID = t.questID
        val quest = questID.getQuestFrame()?: return
        val target = t.getTargetFrame()?: return
        runEval(ev.player, quest, target, QueueType.FINISH)
        if (ev.modeType == ModeType.COLLABORATION) {
            p.teamData()?.playerMembers(false)?.forEach {
                it.targetData(questID, t.id)?.state = StateType.FINISH
            }
        }
        if (p.completedTargets(questID, ev.modeType)) {
            p.finishQuest(questID)
        }
    }

    fun runEval(player: Player, quest: QuestFrame, target: TargetFrame, type: QueueType) {
        val mode = quest.mode
        if (mode.type == ModeType.COLLABORATION && player.teamData()?.isLeader(player) == false) {
            return
        }
        target.trigger.forEach {
            if (it.type == type) {
                runEvalSet(it.select.objective(player), it.script)
            }
        }
    }

    @SubscribeEvent
    fun track(ev: TargetEvent.Track) {
        val target = ev.targetData.getTargetFrame()?: return
        runEval(ev.player, ev.questFrame, target, QueueType.TRACK)
    }

}