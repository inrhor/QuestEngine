package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.ConditionType
import cn.inrhor.questengine.api.quest.TargetExtend
import org.bukkit.event.block.BlockBreakEvent

class TargetBreakBlock: TargetExtend<BlockBreakEvent>() {

    override val name = "break block"

    override var event = BlockBreakEvent::class

    override var reward = "xx"

    override var time = -1

    override var schedule = 0

    init {
        tasker{
            player
        }
        val block = object: ConditionType("block"){
            override fun check(): Boolean {
                return (schedule >= 3)
            }
        }
        addCondition("block", block)
    }

}