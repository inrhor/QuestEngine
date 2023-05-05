package cn.inrhor.questengine.api.target.util

import cn.inrhor.questengine.api.manager.DataManager.doingTargets
import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.target.node.ObjectiveNode
import cn.inrhor.questengine.script.kether.runEval
import cn.inrhor.questengine.utlis.bukkit.ItemMatch
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.common5.Demand

object TriggerUtils {

    /**
     * 玩家触发目标
     *
     * @return Player
     */
    fun Player.triggerTarget(event: String, check: (TargetFrame, ObjectiveNode) -> Boolean = { _, _ -> true }): Player {
        doingTargets(event).forEach {
            val target = it.getTargetFrame()?: return@forEach
            if (check(target, target.pass)) {
                Schedule.run(this, it, target.pass.amount)
            }
        }
        return this
    }

    /*fun idTrigger(target: TargetFrame, id: String, meta: String = "id"): Boolean {
        val idCon = target.nodeMeta(meta)
        idCon.forEach {
            if (id == it) return true
        }
        return false
    }

    *//**
     * 条件列表
     *
     * @return 布尔值，空或满足返回true
     *//*
    fun booleanTrigger(player: Player, targetData: TargetData, target: TargetFrame, run: Boolean = true, amount: Int = 1): Boolean {
        val needCondition = target.nodeMeta("need")
        if (needCondition.isEmpty() || runEval(player, needCondition)) {
            if (run) Schedule.run(player, targetData, amount)
            return true
        }
        return false
    }*/

    /**
     * 物品匹配器
     */
    fun itemTrigger(pass: ObjectiveNode, itemStack: ItemStack, inventory: Inventory): Boolean {
        val content = pass.item
        if (content.isEmpty()) return true
        content.forEach {
            if (ItemMatch(Demand(it)).checkItem(itemStack, inventory)) return true
        }
        return false
    }

}