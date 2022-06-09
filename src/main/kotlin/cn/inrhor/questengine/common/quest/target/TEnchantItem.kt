package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.api.target.util.TriggerUtils.itemTrigger
import cn.inrhor.questengine.api.target.util.TriggerUtils.numberTrigger
import cn.inrhor.questengine.common.database.data.doingTargets
import org.bukkit.event.enchantment.EnchantItemEvent

object TEnchantItem: TargetExtend<EnchantItemEvent>() {

    override val name = "enchant item"

    init {
        event = EnchantItemEvent::class
        tasker{
            enchanter.doingTargets(name).forEach {
                val target = it.getTargetFrame()
                if (itemTrigger(target, item) &&
                    numberTrigger(target, "cost", expLevelCost.toDouble())) {
                    Schedule.isNumber(enchanter, name, "number", it)
                }
            }
            enchanter
        }
    }

}