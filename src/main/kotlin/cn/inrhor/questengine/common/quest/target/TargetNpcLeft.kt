package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.ConditionType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.api.target.util.TriggerUtils
import net.citizensnpcs.api.event.NPCLeftClickEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object TargetNpcLeft: TargetExtend<NPCLeftClickEvent>() {

    override val name = "left npc"

    init {
        if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
            event = NPCLeftClickEvent::class
            tasker {
                val player = clicker
                match(player, npc.id.toString())
                player
            }
            // 注册
            TargetManager.register(name, "id", mutableListOf("id"))
            TargetManager.register(name, "need", mutableListOf("need"))
        }
    }

    fun match(player: Player, npcID: String) {
        val questData = QuestManager.getDoingQuest(player) ?: return
        if (!QuestManager.matchQuestMode(questData)) return
        val innerData = questData.questInnerData
        val targetData = QuestManager.getDoingTarget(player, name) ?: return
        val innerTarget = targetData.questTarget
        val id = object : ConditionType(mutableListOf("id")) {
            override fun check(): Boolean {
                return TriggerUtils.idTrigger(innerTarget, npcID)
            }
        }
        val need = object : ConditionType("need") {
            override fun check(): Boolean {
                return TriggerUtils.booleanTrigger(player, questData, targetData, innerData)
            }
        }
        // 刷新
        TargetManager.set(name, "id", id)
        TargetManager.set(name, "need", need)
    }

}