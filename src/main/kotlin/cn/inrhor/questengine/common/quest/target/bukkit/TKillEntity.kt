package cn.inrhor.questengine.common.quest.target.bukkit

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.TriggerUtils.triggerTarget
import org.bukkit.event.entity.EntityDeathEvent

/**
 * 已重写
 */
object TKillEntity: TargetExtend<EntityDeathEvent>() {

    override val name = "player kill entity"

    init {
        event = EntityDeathEvent::class
        tasker {
            val player = entity.killer?: return@tasker null
            player.triggerTarget(name) { _, pass ->
                val entityTypes = pass.entityTypes
                val name = pass.name
                pass.exp >= droppedExp &&
                        (pass.entityTypes.isEmpty() || entityTypes.contains(entityType.name)) &&
                        (name.isEmpty() || name.contains(entity.customName))
            }
        }
    }

}