package cn.inrhor.questengine.common.listener.quest

import cn.inrhor.questengine.api.event.TargetEvent
import cn.inrhor.questengine.api.manager.DataManager.completedTargets
import cn.inrhor.questengine.api.manager.DataManager.targetData
import cn.inrhor.questengine.api.manager.DataManager.teamData
import cn.inrhor.questengine.common.quest.enum.ModeType
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.quest.manager.QuestManager.finishQuest
import taboolib.common.platform.event.SubscribeEvent

object TargetListener {

    @SubscribeEvent
    fun finish(ev: TargetEvent.Finish) {
        val p = ev.player
        val t = ev.targetData
        t.state = StateType.FINISH
        val questID = t.questID
        if (ev.modeType == ModeType.COLLABORATION) {
            p.teamData()?.playerMembers(false)?.forEach {
                it.targetData(questID, t.id)?.state = StateType.FINISH
            }
        }
        if (p.completedTargets(questID, ev.modeType)) {
            p.finishQuest(questID)
        }
    }

}