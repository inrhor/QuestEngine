package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.ConditionType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.api.target.util.ClickNPC
import ink.ptms.adyeshach.api.event.AdyeshachEntityDamageEvent
import org.bukkit.Bukkit
import java.util.*

object TargetLeftAdyItem: TargetExtend<AdyeshachEntityDamageEvent>() {

    override val name = "give ady-left item"

    init {
        if (Bukkit.getPluginManager().getPlugin("Adyeshach") != null) {
            event = AdyeshachEntityDamageEvent::class
            tasker {
                val questData = QuestManager.getDoingQuest(player) ?: return@tasker player
                if (!QuestManager.matchQuestMode(questData)) return@tasker player
                val innerData = questData.questInnerData
                val innerTarget = QuestManager.getDoingTarget(player, TargetNpcRightItem.name) ?: return@tasker player
                val id = object : ConditionType(mutableListOf("id")) {
                    override fun check(): Boolean {
                        return (ClickNPC.idTrigger(innerTarget, entity.id))
                    }
                }
                val item = object : ConditionType("item") {
                    override fun check(): Boolean {
                        return (ClickNPC.itemTrigger(player, questData, innerTarget, innerData))
                    }
                }
                TargetManager.set(TargetNpcRightItem.name, "id", id)
                TargetManager.set(TargetNpcRightItem.name, "item", item)
                player
            }
            TargetManager.register(TargetNpcRightItem.name, "id", mutableListOf("id"))
            TargetManager.register(TargetNpcRightItem.name, "item", "item")
        }
    }

}