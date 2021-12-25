package cn.inrhor.questengine

import cn.inrhor.questengine.utlis.variableReader
import org.bukkit.event.player.PlayerDropItemEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.util.VariableReader

object Test {

    @SubscribeEvent
    fun drop(ev: PlayerDropItemEvent) {
        val p = ev.player
    }

}