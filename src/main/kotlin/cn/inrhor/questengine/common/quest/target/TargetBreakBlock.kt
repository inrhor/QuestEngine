package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.ConditionType
import cn.inrhor.questengine.api.quest.QuestManager
import cn.inrhor.questengine.api.quest.TargetExtend
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.QuestState
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
                val player = event.objectInstance!!.player
                val questData = QuestManager.getDoingQuest(player)?: return false
                val questID = questData.questID
                val mainID = questData.questMainData.mainQuestID
                val mainModule = QuestManager.getMainQuestModule(questID, mainID)?: return false
                val target = QuestManager.getDoingTarget(player, name)?: return false
                val targetModule = mainModule.questTargetList[name]

                return true
            }
        }
        addCondition("block", block)
        TargetManager.register(name, this)
    }

}