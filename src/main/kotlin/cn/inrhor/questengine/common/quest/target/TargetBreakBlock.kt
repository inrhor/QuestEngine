package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.ConditionType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.quest.TargetExtend
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.QuestInnerData
import cn.inrhor.questengine.common.quest.QuestTarget
import cn.inrhor.questengine.common.quest.manager.RewardManager
import cn.inrhor.questengine.common.quest.manager.TargetManager
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import java.util.*

object TargetBreakBlock: TargetExtend<BlockBreakEvent>() {

    override val name = "break block"

    override var event = BlockBreakEvent::class

    init {
        tasker{
            val block = object: ConditionType("block") {
                override fun check(): Boolean {
                    val questData = QuestManager.getDoingQuest(player)?: return false
                    if (!QuestManager.matchQuestMode(questData)) return false
                    val innerData = questData.questInnerData
                    val innerTarget = QuestManager.getDoingTarget(player, name)?: return false
                    val breakBlock = block.type
                    return (targetTrigger(player, questData, breakBlock, innerTarget, innerData))
                }
            }
            // 刷新
            TargetManager.register(name, block)
            player
        }
        // 注册
        TargetManager.register(name, ConditionType("block"))
    }

    fun targetTrigger(player: Player, questData: QuestData, breakBlock: Material, target: QuestTarget, questInnerData: QuestInnerData): Boolean {
        val blockCondition = target.condition["block"]?: return false
        val sp = blockCondition.split(" ")
        val material = sp[0].uppercase(Locale.getDefault())
        val amount = sp[1].toInt()
        if (material == breakBlock.name) {
            val targetData = questInnerData.targetsData[name]?: return false
            targetData.schedule = targetData.schedule + 1
            val allSchedule = TargetManager.scheduleUtil(name, questData, targetData)
            return RewardManager.finishReward(player, questData, questInnerData, target, amount, allSchedule)
        }
        return true
    }

}