package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.TargetExtend
import org.bukkit.event.block.BlockPlaceEvent

object TPlaceBlock: TargetExtend<BlockPlaceEvent>() {

    override val name = "place block"

    init {
        event = BlockPlaceEvent::class
        tasker{
            TBreakBlock.block(player, name, blockPlaced.type)
            player
        }
    }

}