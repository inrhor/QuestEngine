package cn.inrhor.questengine.common.quest.target.bukkit

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.TriggerUtils.triggerTarget
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent

object TBreakBlock: TargetExtend<BlockBreakEvent>() {

    override val name = "break block"

    init {
        event = BlockBreakEvent::class
        tasker {
            blockMatch(player, name, block.type, block.location, expToDrop)
        }
    }

    fun blockMatch(player: Player, name: String, blockMaterial: Material, location: Location, exp: Int = 0): Player {
        return player.triggerTarget(name) { _, pass ->
            val material = pass.material
            if (material.isEmpty() || material.contains(blockMaterial.name)) {
                if (pass.exp <= exp) {
                    if (pass.locCheck(player, location)) {
                        return@triggerTarget true
                    }
                }
            }
            return@triggerTarget false
        }
    }

}