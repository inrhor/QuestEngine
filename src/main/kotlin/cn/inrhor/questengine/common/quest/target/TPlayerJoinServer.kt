package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.api.manager.DataManager.doingTargets
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent

object TPlayerJoinServer: TargetExtend<PlayerJoinEvent>() {

    override val name = "player join server"

    init {
        event = PlayerJoinEvent::class
        tasker{
            match(player, name)
            player
        }
    }

    fun match(player: Player, name: String) {
        player.doingTargets(name).forEach {
            Schedule.isNumber(player, "number", it)
        }
    }

}