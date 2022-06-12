package cn.inrhor.questengine.common.listener.quest

import cn.inrhor.questengine.api.event.QuestEvent
import taboolib.common.platform.event.SubscribeEvent

object QuestListener {

    @SubscribeEvent
    fun accept(ev: QuestEvent.Accept) {
        val p = ev.player
        val quest = ev.questFrame
        if (!quest.allowTime(p)) return
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
        ev.questFrame.runEval(ev.player, ev.queueType)
    }

}