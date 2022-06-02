package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.api.target.util.TriggerUtils.itemTrigger
import cn.inrhor.questengine.api.target.util.TriggerUtils.numberTrigger
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.event.enchantment.EnchantItemEvent

object TEnchantItem: TargetExtend<EnchantItemEvent>() {

    override val name = "enchant item"

    init {
        event = EnchantItemEvent::class
        tasker{
            QuestManager.getDoingTargets(enchanter, name).forEach {
                if (itemTrigger(it.questTarget, item) &&
                    numberTrigger(it.questTarget, "cost", expLevelCost.toDouble())) {
                    Schedule.isNumber(enchanter, name, "number", it)
                }
            }
            enchanter
        }
    }

}