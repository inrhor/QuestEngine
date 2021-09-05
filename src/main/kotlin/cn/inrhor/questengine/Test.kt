package cn.inrhor.questengine

import cn.inrhor.questengine.common.quest.ui.QuestSortManager
import org.bukkit.event.player.PlayerDropItemEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.sendBook

object Test {

    @SubscribeEvent
    fun drop(ev: PlayerDropItemEvent) {
        ev.player.sendBook {
            writeRaw(QuestSortManager.jsonUI["home"]!!)
        }
    }

}