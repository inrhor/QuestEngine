package cn.inrhor.questengine.api.target.util

import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.script.kether.runEval
import cn.inrhor.questengine.utlis.bukkit.ItemMatch
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.common5.Demand

object TriggerUtils {

    fun idTrigger(target: TargetFrame, id: String): Boolean {
        val idCon = target.nodeMeta("id")
        idCon.forEach {
            if (id == it) return true
        }
        return false
    }

    /**
     * 条件列表
     *
     * @return 布尔值，空或满足返回true
     */
    fun booleanTrigger(player: Player, targetData: TargetData, target: TargetFrame, run: Boolean = true, amount: Int = 1): Boolean {
        val needCondition = target.nodeMeta("need")?: mutableListOf()
        if (needCondition.isEmpty() || runEval(player, needCondition)) {
            if (run) Schedule.run(player, targetData, amount)
            return true
        }
        return false
    }

    /**
     * @param tag 键
     * @return 是否满足大于或等于数字
     */
    fun numberTrigger(target: TargetFrame, tag: String, get: Double): Boolean {
        val number = target.nodeMeta(tag) ?: return false
        return (number[0].toDouble() >= get)
    }

    /**
     * 物品匹配器
     */
    fun itemTrigger(target: TargetFrame, itemStack: ItemStack, inventory: Inventory): Boolean {
        val content = target.nodeMeta("item")
        content.forEach {
            if (ItemMatch(Demand(it)).checkItem(itemStack, inventory)) return true
        }
        return false
    }

}