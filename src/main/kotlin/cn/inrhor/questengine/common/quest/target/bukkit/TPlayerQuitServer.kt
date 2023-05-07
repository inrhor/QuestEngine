package cn.inrhor.questengine.common.quest.target.bukkit

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.TriggerUtils.triggerTarget
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.EventPriority

object TPlayerQuitServer: TargetExtend<PlayerQuitEvent>() {

    override val name = "player quit server"

    override var priority = EventPriority.HIGHEST

    init {
        event = PlayerQuitEvent::class
        tasker{
            player.triggerTarget(TPlayerJoinServer.name) { _, _ -> true }
        }
    }

}