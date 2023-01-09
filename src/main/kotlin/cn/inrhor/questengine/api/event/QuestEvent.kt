package cn.inrhor.questengine.api.event

import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.api.quest.QueueType
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class QuestEvent {

    class Accept(val player: Player, val questFrame: QuestFrame, val queueType: QueueType = QueueType.ACCEPT): BukkitProxyEvent()

    class Finish(val player: Player, val questFrame: QuestFrame, val queueType: QueueType = QueueType.FINISH): BukkitProxyEvent()

    class Quit(val player: Player, val questFrame: QuestFrame, val queueType: QueueType = QueueType.QUIT): BukkitProxyEvent()

    class Reset(val player: Player, val questFrame: QuestFrame, val queueType: QueueType = QueueType.RESET): BukkitProxyEvent()

    class Track(val player: Player, val questFrame: QuestFrame, val queueType: QueueType = QueueType.TRACK): BukkitProxyEvent()

    class Fail(val player: Player, val questFrame: QuestFrame, val queueType: QueueType = QueueType.FAIL): BukkitProxyEvent()

}