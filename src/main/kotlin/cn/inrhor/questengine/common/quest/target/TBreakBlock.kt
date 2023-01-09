package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.manager.DataManager.doingTargets
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
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
            val target = it.getTargetFrame()?: return@forEach
            val block = target.nodeMeta("block") ?: return
            val material = block.toList()
            val am = target.nodeMeta("amount") ?: return
            val amount = am[0].toInt()
            if (material.contains(blockMaterial.name)) {
                Schedule.run(player, it, amount)
            }
        }
    }

}