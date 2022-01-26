package cn.inrhor.questengine.api.event

import cn.inrhor.questengine.common.database.data.PacketData
import cn.inrhor.questengine.script.kether.runEval
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class PacketEntityInteractEvent(val player: Player, val packetData: PacketData, val type: Type): BukkitProxyEvent() {

    enum class Type {
        LEFT, RIGHT
    }

    /**
     * 检测 passOnly 一旦符合就结束并增加 clickCount 数值
     * 上述符合的就检测 passAdd，符合的增加，不符合的不增加
     */
    fun pass(): Boolean {
        if (!passOnly()) return false
        passAdd()
        return true
    }

    private fun passOnly(): Boolean {
        val list = packetData.clickAction.passOnly
        if (list.isEmpty()) return true
        list.forEach {
            if (runEval(player, "$it to "+packetData.entityID)) return true
        }
        return false
    }

    private fun passAdd() {
        packetData.clickAction.passAdd.forEach {
            runEval(player, "$it to "+packetData.entityID)
        }
    }

}