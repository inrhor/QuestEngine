package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.ConditionType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.QuestInnerData
import cn.inrhor.questengine.common.quest.QuestTarget
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.api.target.util.Schedule
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import java.util.*

object TargetBreakBlock: TargetExtend<BlockBreakEvent>() {

    override val name = "break block"

    init {
        event = BlockBreakEvent::class
        tasker{
            // 刷新
            TargetManager.set(name, "block", block(player, name, block.type))
            player
        }
        // 注册
        TargetManager.register(name, "block", "block")
    }

    fun block(player: Player, name: String, blockMaterial: Material): ConditionType {
        return object : ConditionType("block") {
            override fun check(): Boolean {
                val questData = QuestManager.getDoingQuest(player) ?: return false
                if (!QuestManager.matchQuestMode(questData)) return false
                val innerData = questData.questInnerData
                val innerTarget = QuestManager.getDoingTarget(player, name) ?: return false
                return (targetTrigger(player, name, questData, blockMaterial, innerTarget, innerData))
            }
        }
    }

    fun targetTrigger(player: Player, name: String, questData: QuestData, blockMaterial: Material, target: QuestTarget, questInnerData: QuestInnerData): Boolean {
        val blockCondition = target.condition["block"]?: return false
        val sp = blockCondition.split(" ")
        val material = sp[0].uppercase(Locale.getDefault())
        val amount = sp[1].toInt()
        if (material == blockMaterial.name) {
            val targetData = questInnerData.targetsData[name]?: return false
            return Schedule.run(player, name, questData, questInnerData, target, targetData, amount)
        }
        return true
    }

}