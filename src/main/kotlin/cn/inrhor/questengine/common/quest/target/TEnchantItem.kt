package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.api.target.util.TriggerUtils.itemTrigger
import cn.inrhor.questengine.api.target.util.TriggerUtils.numberTrigger
import cn.inrhor.questengine.api.manager.DataManager.doingTargets
import org.bukkit.event.enchantment.EnchantItemEvent

object TEnchantItem: TargetExtend<EnchantItemEvent>() {

    override val name = "enchant item"

    init {
        event = EnchantItemEvent::class
        tasker{
            enchanter.doingTargets(name).forEach {
                val target = it.getTargetFrame()?: return@forEach
                if (itemTrigger(target, item, inventory) &&
                    numberTrigger(target, "cost", expLevelCost.toDouble())) {
                    Schedule.isNumber(enchanter, "number", it)
                }
            }
            enchanter
        }
    }

}