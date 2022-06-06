package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.module.QuestTarget
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.script.kether.runEval

import cn.inrhor.questengine.utlis.subAfter
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent

object TKillEntity: TargetExtend<EntityDeathEvent>() {

    override val name = "player kill entity"


    init {
        event = EntityDeathEvent::class
        tasker{
            val player = entity.killer?: return@tasker null
            QuestManager.getDoingTargets(player, name).forEach {
                if (checkEntity(it.questTarget, entityType) && checkCondition(player, it.questTarget, entity, droppedExp)) {
                    Schedule.isNumber(player, name, "number", it)
                }
            }
            player
        }
    }

    private fun checkEntity(target: QuestTarget, type: EntityType): Boolean {
        val condition = target.nodeMeta("entity")?: return false
        return when (condition[0].uppercase()) {
            "PLAYER" -> type == EntityType.PLAYER
            else -> type != EntityType.PLAYER
        }
    }

    private fun checkCondition(player: Player, target: QuestTarget, entity: Entity, dropExp: Int): Boolean {
        val condition = target.nodeMeta("condition")?: return false
        val checkNumber = (target.nodeMeta("check")?: listOf("0"))[0].toInt()
        var i = 0
        condition.forEach {
            if (!checkNumber(checkNumber, i)) return true
            val s = it.lowercase()
            if (s.startsWith("entity name")) {
                val get = it.subAfter("@")
                if (!matchName(player, entity, get)) return false
            }else if (s.startsWith("eval ")) {
                val get = it.subAfter("@").replace("dropExp", dropExp.toString(), true)
                if (!runEval(player, get)) return false
            }
            i++
        }
        return true
    }

    fun matchName(player: Player, entity: Entity, str: String): Boolean {
        if (runEval(player, "strMatch type $str *'${entity.name}'")) return true
        if (runEval(player, "strMatch type $str *'${entity.customName}'")) return true
        return false
    }

    private fun checkNumber(check: Int, forEach: Int): Boolean {
        if (check <= 0 || check >= forEach) return true
        return false
    }

}