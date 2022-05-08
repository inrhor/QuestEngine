package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
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
        val questData = QuestManager.getDoingQuest(player, true)?: return
        Schedule.isNumber(player, name, "number", questData)
    }

}