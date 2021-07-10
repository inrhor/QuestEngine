package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.ConditionType
import cn.inrhor.questengine.api.quest.QuestManager
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
                val ev = event.objectInstance?: return false
                val player = ev.player
                val questData = QuestManager.getDoingQuest(player)?: return false
                val questID = questData.questID
                val mainData = questData.questMainData
                val mainTarget = QuestManager.getDoingMainTarget(player, name)
                val breakBlock = ev.block.type
                if (mainTarget != null) {
                    val blockCondition = mainTarget.condition["block"]?: return false
                    val sp = blockCondition.split(" ")
                    val material = sp[0]
                    val amount = sp[1].toInt()
                    if (material == breakBlock.name) {
                        val schedule = mainData.schedule[name] ?: mainData.schedule.put(name, 0)
                        if (schedule != null) {
                            mainData.schedule[name] = schedule + 1
                            if (amount >= schedule) {
                                val mainModule = QuestManager.getMainQuestModule(questID, mainData.mainQuestID)?: return false
                                val rewardRepeatState = mainData.rewardState[name]?: return false
                                TargetManager.finishReward(mainData, mainModule.questReward, mainTarget.reward, rewardRepeatState)
                            }
                        }
                    }
                }

                val tg = QuestManager.getDoingSubTarget(player, name)?: return false
                val subQuestID= tg.subQuestID
                val subData = QuestManager.getSubQuestData(player, questID, subQuestID)?: return false
                val subTarget = tg.questTarget
                val blockCondition = subTarget.condition["block"]?: return false
                val sp = blockCondition.split(" ")
                val material = sp[0]
                val amount = sp[1].toInt()
                if (material == breakBlock.name) {
                    val schedule = subData.schedule[name] ?: subData.schedule.put(name, 0)
                    if (schedule != null) {
                        subData.schedule[name] = schedule + 1
                        if (amount >= schedule) {
                            val subModule = QuestManager.getSubQuestModule(questID, subData.mainQuestID, subQuestID)?: return false
                            val rewardRepeatState = subData.rewardState[name]?: return false
                            TargetManager.finishReward(subData, subModule.questReward, subTarget.reward, rewardRepeatState)
                        }
                    }
                }

                return true
            }
        }
        addCondition("block", block)
        TargetManager.register(name, this)
    }

}