package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.event.CollectionPassEvent
import cn.inrhor.questengine.api.packet.PacketModule
import cn.inrhor.questengine.api.target.ConditionType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.quest.QuestTarget
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.api.target.util.Schedule

object TargetClickCollection: TargetExtend<CollectionPassEvent>() {

    override val name = "pass collection packet"

    init {
        event = CollectionPassEvent::class
        tasker{
            val questData = QuestManager.getDoingQuest(player) ?: return@tasker player
            if (!QuestManager.matchQuestMode(questData)) return@tasker player
            val innerData = questData.questInnerData
            val targetData = QuestManager.getDoingTarget(player, name) ?: return@tasker player
            val innerTarget = targetData.questTarget
            val packetID = object : ConditionType("packetID") {
                override fun check(): Boolean {
                    return checkPacketID(innerTarget, packetData.packetModule)
                }
            }
            val number = object: ConditionType("number") {
                override fun check(): Boolean {
                    return Schedule.isNumber(player, name, "number", questData, innerData, innerTarget)
                }
            }
            TargetManager.set(name, "packetID", packetID)
            TargetManager.set(name, "number", number)
            player
        }
        TargetManager.register(name, "packetID", "packetID")
        TargetManager.register(name, "number", "number")
    }

    fun checkPacketID(target: QuestTarget, packetModule: PacketModule): Boolean {
        val id = target.condition["packetID"]?: return false
        return id == packetModule.packedID
    }

}