package cn.inrhor.questengine.common.listener.quest

import cn.inrhor.questengine.api.event.QuestEvent
import cn.inrhor.questengine.api.manager.DataManager.questData
import cn.inrhor.questengine.api.manager.DataManager.setTrackingData
import cn.inrhor.questengine.common.quest.enum.StateType
import taboolib.common.platform.event.SubscribeEvent

object QuestListener {

    @SubscribeEvent
    fun accept(ev: QuestEvent.Accept) {
        val p = ev.player
        val quest = ev.questFrame
        if (!quest.allowTime()) return
        quest.runEval(p, ev.queueType)
    }

    @SubscribeEvent
    fun finish(ev: QuestEvent.Finish) {
        ev.questFrame.runEval(ev.player, ev.queueType)
    }

    @SubscribeEvent
    fun quit(ev: QuestEvent.Quit) {
        ev.questFrame.runEval(ev.player, ev.queueType)
    }

    @SubscribeEvent
    fun reset(ev: QuestEvent.Reset) {
        ev.questFrame.runEval(ev.player, ev.queueType)
    }

    @SubscribeEvent
    fun fail(ev: QuestEvent.Fail) {
        ev.questFrame.runEval(ev.player, ev.queueType)
    }

    @SubscribeEvent
    fun track(ev: QuestEvent.Track) {
        val frame = ev.questFrame
        val p = ev.player
        val id = frame.id
        p.setTrackingData(id)
        val data = p.questData(id)?: return
        if (data.state != StateType.DOING) return
        frame.runEval(ev.player, ev.queueType)
    }

}