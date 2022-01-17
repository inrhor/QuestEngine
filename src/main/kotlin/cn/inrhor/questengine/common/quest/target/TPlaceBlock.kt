package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.quest.manager.TargetManager
import org.bukkit.event.block.BlockPlaceEvent

object TPlaceBlock: TargetExtend<BlockPlaceEvent>() {

    override val name = "place block"

    init {
        event = BlockPlaceEvent::class
        tasker{
            val block = TBreakBlock.block(player, name, blockPlaced.type)
            TargetManager.set(name, "block", block)
            player
        }
        TargetManager.register(name, "block")
    }

}