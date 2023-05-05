package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.TriggerUtils.addProgress
import cn.inrhor.questengine.api.target.util.TriggerUtils.triggerTarget
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent

object TBreakBlock: TargetExtend<BlockBreakEvent>() {

    override val name = "break block"

    init {
        event = BlockBreakEvent::class
        tasker {
            blockMatch(player, name, block.type, expToDrop)
            player
        }
    }

    fun blockMatch(player: Player, name: String, blockMaterial: Material, exp: Int = 0) {
        val t = player.triggerTarget(name) {
            val pass = it.pass
            val material = pass.material
            if ((material.isNotEmpty() && !material.contains(blockMaterial.name)) || pass.exp < exp) {
                return@triggerTarget false
            }

        }
        player.addProgress(t, 1)
    }

}