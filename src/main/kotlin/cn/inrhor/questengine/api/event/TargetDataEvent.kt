package cn.inrhor.questengine.api.event

import cn.inrhor.questengine.common.database.data.quest.TargetData
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * 任务目标条目数据操作事件
 */
class TargetDataEvent {

    /**
     * 增加进度
     */
    class AddProgress(val player: Player, val targetData: TargetData, val addProgress: Int): BukkitProxyEvent()

    /**
     * 设置进度
     */
    class SetProgress(val player: Player, val targetData: TargetData, val setProgress: Int): BukkitProxyEvent()

    /**
     * 设置状态
     */
    class SetState(val player: Player, val targetData: TargetData): BukkitProxyEvent()

}