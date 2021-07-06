package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.ConditionType
import cn.inrhor.questengine.api.quest.TargetExtend
import cn.inrhor.questengine.common.quest.TargetManager
import org.bukkit.event.block.BlockBreakEvent

class TargetBreakBlock(override var finishReward: String,
                       override var time: String,
                       override var schedule: Int): TargetExtend<BlockBreakEvent>() {

    override val name = "break block"

    override var event = BlockBreakEvent::class

    init {
        tasker{
            player
        }
        val block = object: ConditionType("block"){
            override fun check(): Boolean {
                return (schedule >= playerData....get)
            }
        }
        addCondition("block", block)
        TargetManager().register(name, this)
    }

}