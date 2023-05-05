package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.api.target.util.TriggerUtils.itemTrigger
import cn.inrhor.questengine.api.target.util.TriggerUtils.numberTrigger
import cn.inrhor.questengine.api.manager.DataManager.doingTargets
import cn.inrhor.questengine.api.target.util.TriggerUtils.triggerTarget
import org.bukkit.event.enchantment.EnchantItemEvent

object TEnchantItem: TargetExtend<EnchantItemEvent>() {

    override val name = "enchant item"

    init {
        event = EnchantItemEvent::class
        tasker{
            enchanter.triggerTarget(name) {
                val pass = it.pass
                val cost = pass.cost
                itemTrigger(pass, item, inventory) &&
                        (cost <= 0.0 || cost >= expLevelCost)
            }
            enchanter
        }
    }

}