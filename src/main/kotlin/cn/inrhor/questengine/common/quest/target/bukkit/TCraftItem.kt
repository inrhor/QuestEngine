package cn.inrhor.questengine.common.quest.target.bukkit

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.TriggerUtils.itemTrigger
import cn.inrhor.questengine.api.target.util.TriggerUtils.triggerTarget
import org.bukkit.entity.Player
import org.bukkit.event.inventory.CraftItemEvent

object TCraftItem : TargetExtend<CraftItemEvent>() {

    override val name = "craft item"

    init {
        event = CraftItemEvent::class
        tasker {
            val p = whoClicked as Player
            val item = inventory.result ?: return@tasker p
            if (isShiftClick) {
                var itemChecked = 0
                var possibleCreations = 1
                inventory.matrix.forEach { matrixItem ->
                    // 如果不是空的或者不是空气
                    if (matrixItem != null && !matrixItem.type.isAir) {
                        possibleCreations = if (itemChecked == 0) {
                            matrixItem.amount
                        } else {
                            possibleCreations.coerceAtMost(matrixItem.amount)
                        }
                        itemChecked++
                    }
                }
                // 获得实际合成次数
                val resultAmount = recipe.result.amount * possibleCreations
                p.triggerTarget(name, resultAmount) { _, pass ->
                    itemTrigger(pass, item, inventory)
                }
            } else {
                p.triggerTarget(name) { _, pass ->
                    itemTrigger(pass, item, inventory)
                }
            }
        }
    }

}