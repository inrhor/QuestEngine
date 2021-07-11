package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.ConditionType
import cn.inrhor.questengine.api.quest.QuestManager
import cn.inrhor.questengine.api.quest.TargetExtend
import cn.inrhor.questengine.common.database.data.quest.QuestOpenData
import cn.inrhor.questengine.common.quest.QuestTarget
import cn.inrhor.questengine.common.quest.TargetManager
import org.bukkit.Material
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
                val ev = event.objectInstance?: return false
                val player = ev.player
                val questData = QuestManager.getDoingQuest(player)?: return false
                val questID = questData.questID
                val mainData = questData.questMainData
                val mainTarget = QuestManager.getDoingMainTarget(player, name)?: return false
                val breakBlock = ev.block.type
                targetTrigger(questID, breakBlock, mainTarget, mainData)

                val tg = QuestManager.getDoingSubTarget(player, name)?: return false
                val subQuestID= tg.subQuestID
                val subData = QuestManager.getSubQuestData(player, questID, subQuestID)?: return false
                val subTarget = tg.questTarget
                targetTrigger(questID, breakBlock, subTarget, subData)

                return true
            }
        }
        addCondition("block", block)
        TargetManager.register(name, this)
    }

    fun targetTrigger(questID: String, breakBlock: Material, target: QuestTarget, data: QuestOpenData) {
        val blockCondition = target.condition["block"]?: return
        val sp = blockCondition.split(" ")
        val material = sp[0]
        val amount = sp[1].toInt()
        if (material == breakBlock.name) {
            val schedule = data.schedule[name] ?: data.schedule.put(name, 0)
            if (schedule != null) {
                data.schedule[name] = schedule + 1
                if (amount >= schedule) {
                    val mainModule = QuestManager.getMainQuestModule(questID, data.mainQuestID)?: return
                    val rewardRepeatState = data.rewardState[name]?: return
                    TargetManager.finishReward(data, mainModule.questReward, target.reward, rewardRepeatState)
                }
            }
        }
    }

}