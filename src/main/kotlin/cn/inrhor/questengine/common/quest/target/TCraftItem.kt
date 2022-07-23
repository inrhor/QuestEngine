package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.api.target.util.TriggerUtils.itemTrigger
import cn.inrhor.questengine.common.database.data.doingTargets
import cn.inrhor.questengine.utlis.bukkit.ItemCheck
import org.bukkit.entity.Player
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack

object TCraftItem: TargetExtend<CraftItemEvent>() {

    override val name = "craft item"

    init {
        event = CraftItemEvent::class
        tasker{
            val p = whoClicked as Player
            p.doingTargets(name).forEach {
                val target = it.getTargetFrame()
                val item = inventory.result
                val am = target.nodeMeta("amount")?: listOf("1")
                val amount = am[0].toInt()
                if (item != null) {
                    if (itemTrigger(target, item) && matrixItems(target, inventory.matrix)) {
                        Schedule.run(p, name, it, amount)
                    }
                }
            }
            p
        }
    }

    fun matrixItems(target: TargetFrame, matrix: Array<ItemStack>): Boolean {
        val content = target.nodeMeta("matrix")?: return true
        val a = content.toList()
        if (a.isEmpty()) return true
        matrix.forEach {
            if (!itemsMatch(a, it)) return false
        }
        return true
    }

    fun itemsMatch(s: List<String>, itemStack: ItemStack): Boolean {
        s.forEach {
            if (ItemCheck.itemCheckSplit(it).match(itemStack, false)) return true
        }
        return false
    }

}