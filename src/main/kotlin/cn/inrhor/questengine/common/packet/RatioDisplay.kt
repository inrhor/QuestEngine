package cn.inrhor.questengine.common.packet

import cn.inrhor.questengine.common.database.data.PacketData
import cn.inrhor.questengine.utlis.progressBar
import org.bukkit.entity.Player

object RatioDisplay {

    /**
     * 交互时显示比值
     */
    fun appear(player: Player, packetData: PacketData) {
        val module = packetData.packetModule
        val actionModule = module.action?: return
        if (!actionModule.ratioEnable) return
        val action = packetData.clickAction
        val need = action.needClickCount
        val now = action.clickCountLog
        player.sendTitle("",
            progressBar(now, need, 60, "|", "§a", "§7"),
            0, 2, 0)
    }

}