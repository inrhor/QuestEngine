package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.api.target.util.Schedule
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent

object TBreakBlock: TargetExtend<BlockBreakEvent>() {

    override val name = "break block"

    init {
        event = BlockBreakEvent::class
        tasker {
            block(player, name, block.type)
            player
        }
    }

    fun block(player: Player, name: String, blockMaterial: Material) {
        QuestManager.getDoingTargets(player, name).forEach {
            val target = it.questTarget
            val block = target.nodeMeta("block") ?: return
            val material = block.toList()
            val am = target.nodeMeta("amount") ?: return
            val amount = am[1].toInt()
            if (material.contains(blockMaterial.name)) {
                Schedule.run(player, name, it, amount)
            }
        }
    }

    /*fun block(player: Player, name: String, blockMaterial: Material): Boolean {
        val list = QuestManager.getDoingQuest(player)
        if (list.isEmpty()) return false
        QuestManager.getDoingQuest(player).forEach {
            blockMatch(player, name, it, blockMaterial)
        }
        return true
    }

    private fun blockMatch(player: Player, name: String, questData: QuestData, blockMaterial: Material) {
        questData.questInnerData.targetsData.values.forEach { targetData ->
            val target = targetData.questTarget
            val block = target.nodeMeta("block") ?: return
            val material = block.toList()
            val am = target.nodeMeta("amount") ?: return
            val amount = am[1].toInt()
            if (material.contains(blockMaterial.name)) {
                Schedule.run(player, name, questData, targetData, amount)
            }
        }
    }*/

}