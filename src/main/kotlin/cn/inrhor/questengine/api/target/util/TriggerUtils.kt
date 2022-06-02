package cn.inrhor.questengine.api.target.util

import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.api.quest.module.inner.QuestTarget
import cn.inrhor.questengine.script.kether.runEval

import cn.inrhor.questengine.utlis.bukkit.ItemCheck
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object TriggerUtils {

    fun idTrigger(target: QuestTarget, id: String): Boolean {
        val idCon = target.nodeMeta("id")?: return false
        idCon.forEach {
            if (id == it) return true
        }
        return false
    }

    /**
     * 条件列表
     *
     * @return 布尔值，默认true
     */
    fun booleanTrigger(player: Player, questTarget: QuestTarget): Boolean {
        val needCondition = questTarget.nodeMeta("need")?: return false
        return runEval(player, needCondition)
    }

    /**
     * @param tag 键
     * @return 是否满足大于或等于数字
     */
    fun numberTrigger(target: QuestTarget, tag: String, get: Double): Boolean {
        val number = target.nodeMeta(tag) ?: return false
        return (number[0].toDouble() >= get)
    }

    /**
     * 物品匹配器
     */
    fun itemTrigger(target: QuestTarget, itemStack: ItemStack): Boolean {
        val content = target.nodeMeta("item")?: return false
        return ItemCheck.itemCheckSplit(content[0]).match(itemStack, false)
    }

}