package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.event.CollectionPassEvent
import cn.inrhor.questengine.api.packet.PacketModule
import cn.inrhor.questengine.api.target.ConditionType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.quest.module.inner.QuestTarget
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.common.database.data.quest.QuestData

object TClickCollection: TargetExtend<CollectionPassEvent>() {

    override val name = "pass collection packet"

    init {
        event = CollectionPassEvent::class
        tasker{
            val questData = QuestManager.getDoingQuest(player, true) ?: return@tasker player
            val packetID = object : ConditionType("packetID") {
                override fun check(): Boolean {
                    return checkPacketID(questData, packetData.packetModule)
                }
            }
            val number = object: ConditionType("number") {
                override fun check(): Boolean {
                    return Schedule.isNumber(player, name, "number", questData)
                }
            }
            TargetManager.set(name, "packetID", packetID)
            TargetManager.set(name, "number", number)
            player
        }
        TargetManager.register(name, "packetID")
        TargetManager.register(name, "number")
    }

    private fun checkPacketID(questData: QuestData, packetModule: PacketModule): Boolean {
        val target = (QuestManager.getDoingTarget(questData, name)?: return false).questTarget
        val id = target.condition["packetID"]?: return false
        return id == packetModule.packedID
    }

}