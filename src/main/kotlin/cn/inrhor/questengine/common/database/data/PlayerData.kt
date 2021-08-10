package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.api.collaboration.TeamOpen
import cn.inrhor.questengine.common.database.data.quest.QuestData
import java.util.*

/**
 * @param uuid 玩家UUID
 * @param dialogData 对话数据
 * @param questDataList 任务数据集合
 * @param controlData 控制模块集合
 */
class PlayerData(
    val uuid: UUID,
    var teamData: TeamOpen?,
    val dialogData: DialogData,
    var questDataList: MutableMap<UUID, QuestData>, /* QuestUUID 对应 QuestData */
    var controlData: ControlData) {

    constructor(uuid: UUID):
            this(uuid, null,
                DialogData(mutableMapOf(), mutableMapOf(), mutableMapOf()),
                mutableMapOf(),
                ControlData(linkedMapOf(), mutableMapOf())
            )

}