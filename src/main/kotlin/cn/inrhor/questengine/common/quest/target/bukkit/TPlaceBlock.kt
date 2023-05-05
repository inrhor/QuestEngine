package cn.inrhor.questengine.common.quest.target.bukkit

import cn.inrhor.questengine.api.target.TargetExtend
import org.bukkit.event.block.BlockPlaceEvent

object TPlaceBlock: TargetExtend<BlockPlaceEvent>() {

    override val name = "place block"

    init {
        event = BlockPlaceEvent::class
        tasker{
            TBreakBlock.blockMatch(player, name, blockPlaced.type, blockPlaced.location)
        }
    }

}