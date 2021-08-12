package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.ConditionType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.api.target.util.ClickNPC
import net.citizensnpcs.api.event.NPCRightClickEvent
import org.bukkit.Bukkit
import java.util.*

object TargetNpcRightItem: TargetExtend<NPCRightClickEvent>() {

    override val name = "give npc-right item"

    init {
        if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
            tasker {
                val player = clicker
                val questData = QuestManager.getDoingQuest(player) ?: return@tasker player
                if (!QuestManager.matchQuestMode(questData)) return@tasker player
                val innerData = questData.questInnerData
                val innerTarget = QuestManager.getDoingTarget(player, name) ?: return@tasker player
                // 建议注意顺序判断
                val id = object : ConditionType(mutableListOf("id")) {
                    override fun check(): Boolean {
                        return ClickNPC.idTrigger(innerTarget, npc.id.toString())
                    }
                }
                val item = object : ConditionType("item") {
                    override fun check(): Boolean {
                        return ClickNPC.itemTrigger(player, questData, innerTarget, innerData)
                    }
                }
                // 刷新
                TargetManager.set(name, "id", id)
                TargetManager.set(name, "item", item)
                player
            }
            // 注册
            TargetManager.register(name, "id", mutableListOf("id"))
            TargetManager.register(name, "item", "item")
        }
    }

}