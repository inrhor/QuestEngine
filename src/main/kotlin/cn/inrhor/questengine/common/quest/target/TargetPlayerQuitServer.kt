package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.ConditionType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.api.target.util.Schedule
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.EventPriority
import java.util.*

object TargetPlayerQuitServer: TargetExtend<PlayerQuitEvent>() {

    override val name = "player quit server"

    override var priority = EventPriority.HIGHEST

    init {
        event = PlayerQuitEvent::class
        tasker{
            TargetPlayerJoinServer.match(player)
            player
        }
        TargetManager.register(name, "number", "number")
    }

}