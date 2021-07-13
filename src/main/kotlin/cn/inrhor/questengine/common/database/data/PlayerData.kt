package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.api.collaboration.TeamOpen
import cn.inrhor.questengine.common.database.data.quest.QuestControlData
import cn.inrhor.questengine.common.database.data.quest.QuestData
import java.util.*

/**
 * @param uuid 玩家UUID
 * @param dialogData 对话数据
 * @param questDataList 任务数据集合
 * @param controlList 控制脚本集合
 */
class PlayerData(
    val uuid: UUID,
    var teamData: TeamOpen?,
    val dialogData: DialogData,
    var questDataList: MutableMap<String, QuestData>,
    var controlList: MutableMap<String, QuestControlData>/* [questID]-[mainID]-[subID] */
) {

    constructor(uuid: UUID):
            this(uuid, null,
                DialogData(mutableListOf(), mutableListOf(), mutableListOf()),
                mutableMapOf(),
                mutableMapOf())

}