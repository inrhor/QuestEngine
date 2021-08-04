package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.ConditionType
import cn.inrhor.questengine.api.quest.TargetExtend
import cn.inrhor.questengine.common.quest.manager.TargetManager
import org.bukkit.event.block.BlockPlaceEvent

object TargetPlaceBlock: TargetExtend<BlockPlaceEvent>() {

    override val name = "place block"

    override var event = BlockPlaceEvent::class

    init {
        tasker{
            val block = TargetBreakBlock.block(player, name, blockPlaced.type)
            TargetManager.set(name, "block", block)
            player
        }
        TargetManager.register(name, "block", "block")
    }

}