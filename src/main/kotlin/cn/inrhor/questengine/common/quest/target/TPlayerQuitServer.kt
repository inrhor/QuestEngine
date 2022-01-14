package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.quest.manager.TargetManager
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.EventPriority

object TPlayerQuitServer: TargetExtend<PlayerQuitEvent>() {

    override val name = "player quit server"

    override var priority = EventPriority.HIGHEST

    init {
        event = PlayerQuitEvent::class
        tasker{
            TPlayerJoinServer.match(player, name)
            player
        }
        TargetManager.register(name, "number")
    }

}