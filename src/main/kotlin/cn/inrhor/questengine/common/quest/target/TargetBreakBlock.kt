package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.ConditionType
import cn.inrhor.questengine.api.quest.TargetExtend
import cn.inrhor.questengine.common.quest.TargetManager
import org.bukkit.event.block.BlockBreakEvent

object TargetBreakBlock: TargetExtend<BlockBreakEvent>() {

    override val name = "break block"

    override var event = BlockBreakEvent::class

    init {
        tasker{
            player
        }
        val block = object: ConditionType("block") {
            override fun check(): Boolean {
                // player data ...
                return true
            }
        }
        addCondition("block", block)
        TargetManager().register(name, this)
    }

}