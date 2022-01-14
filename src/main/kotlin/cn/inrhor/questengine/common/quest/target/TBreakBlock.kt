package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.ConditionType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.api.target.util.Schedule
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent

object TBreakBlock: TargetExtend<BlockBreakEvent>() {

    override val name = "break block"

    init {
        event = BlockBreakEvent::class
        tasker{
            // 刷新
            TargetManager.set(name, "block", block(player, name, block.type))
            player
        }
        // 注册
        TargetManager.register(name, "block")
    }

    fun block(player: Player, name: String, blockMaterial: Material): ConditionType {
        return object : ConditionType("block") {
            override fun check(): Boolean {
                val questData = QuestManager.getDoingQuest(player, true) ?: return false
                return blockMatch(player, name, questData, blockMaterial)
            }
        }
    }

    private fun blockMatch(player: Player, name: String, questData: QuestData, blockMaterial: Material): Boolean {
        val targetData = QuestManager.getDoingTarget(questData, name)?: return false
        val target = targetData.questTarget
        val blockCondition = target.condition["block"]?: return false
        val sp = blockCondition.split(" ")
        val material = sp[0].uppercase()
        val amount = sp[1].toInt()
        if (material == blockMaterial.name) {
            return Schedule.run(player, name, questData, targetData, amount)
        }
        return true
    }

}