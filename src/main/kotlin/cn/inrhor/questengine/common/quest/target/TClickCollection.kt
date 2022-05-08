package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.event.CollectionPassEvent
import cn.inrhor.questengine.api.packet.PacketModule
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.common.database.data.quest.QuestData

object TClickCollection: TargetExtend<CollectionPassEvent>() {

    override val name = "pass collection packet"

    init {
        event = CollectionPassEvent::class
        tasker{
            val questData = QuestManager.getDoingQuest(player, true) ?: return@tasker player
            if (checkPacketID(questData, packetData.packetModule)) {
                Schedule.isNumber(player, name, "number", questData)
            }
            player
        }
    }

    private fun checkPacketID(questData: QuestData, packetModule: PacketModule): Boolean {
        val target = (QuestManager.getDoingTarget(questData, name)?: return false).questTarget
        val id = target.nodeMeta("packetID")?: return false
        return id[0] == packetModule.packedID
    }

}