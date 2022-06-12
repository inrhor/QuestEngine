package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.common.database.data.doingTargets
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent

object TBreakBlock: TargetExtend<BlockBreakEvent>() {

    override val name = "break block"

    init {
        event = BlockBreakEvent::class
        tasker {
            block(player, name, block.type)
            player
        }
    }

    fun block(player: Player, name: String, blockMaterial: Material) {
        player.doingTargets(name).forEach {
            val target = it.getTargetFrame()
            val block = target.nodeMeta("block") ?: return
            val material = block.toList()
            val am = target.nodeMeta("amount") ?: return
            val amount = am[0].toInt()
            if (material.contains(blockMaterial.name)) {
                Schedule.run(player, name, it, amount)
            }
        }
    }

}