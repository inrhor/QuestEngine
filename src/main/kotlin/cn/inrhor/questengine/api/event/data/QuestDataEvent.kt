package cn.inrhor.questengine.api.event.data

import cn.inrhor.questengine.common.database.data.quest.QuestData
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * 任务数据操作事件
 */
class QuestDataEvent {

    /**
     * 任务数据安装事件
     */
    class Install(val player: Player, val questData: QuestData): BukkitProxyEvent()

    /**
     * 任务数据卸载事件
     */
    class Unload(val player: Player, val questId: String): BukkitProxyEvent()

    /**
     * 修改任务状态事件
     */
    class ToggleState(val player: Player, val questData: QuestData): BukkitProxyEvent()

    /**
     * 修改完成任务事件事件
     */
    class ToggleFinishTime(val player: Player, val questData: QuestData): BukkitProxyEvent()

}