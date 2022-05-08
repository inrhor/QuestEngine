package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.common.database.data.quest.QuestData
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent

object TPlayerDeath: TargetExtend<PlayerDeathEvent>() {

    override val name = "player death"


    init {
        event = PlayerDeathEvent::class
        tasker{
            val player = entity
            val questData = QuestManager.getDoingQuest(player, true)?: return@tasker player
            if (isCause(questData, player.lastDamageCause!!.cause)) {
                Schedule.isNumber(player, name, "number", questData)
            }
            player
        }
    }

    fun isCause(questData: QuestData, death: EntityDamageEvent.DamageCause): Boolean {
        val target = (QuestManager.getDoingTarget(questData, name)?: return false).questTarget
        val idCondition = target.nodeMeta("cause")?: return false
        return idCondition.contains(death.toString())
    }

}