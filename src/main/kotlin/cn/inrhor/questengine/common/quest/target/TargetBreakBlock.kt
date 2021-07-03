package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.ConditionType
import cn.inrhor.questengine.api.quest.TargetExtend
import cn.inrhor.questengine.utlis.public.MsgUtil
import org.bukkit.event.block.BlockBreakEvent

class TargetBreakBlock: TargetExtend<BlockBreakEvent>() {

    override val name = "break block"

    override var event = BlockBreakEvent::class

    override var reward = "xx"

    override var time = -1

    init {
        tasker{
            player
        }
        val block = object: ConditionType("block"){
            override fun check(): Boolean {
                MsgUtil.send("Block!!!!!!!!!!")
                return true
            }
        }
        addCondition("block", block)
    }

}