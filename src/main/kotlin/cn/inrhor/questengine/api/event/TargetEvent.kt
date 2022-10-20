package cn.inrhor.questengine.api.event

import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.enum.ModeType
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class TargetEvent {

    class Finish(val player: Player, val targetData: TargetData, val modeType: ModeType): BukkitProxyEvent()

    class Track(val player: Player, val targetData: TargetData, val questFrame: QuestFrame): BukkitProxyEvent()

}