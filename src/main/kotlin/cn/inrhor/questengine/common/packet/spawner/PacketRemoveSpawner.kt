package cn.inrhor.questengine.common.packet.spawner

import cn.inrhor.questengine.api.packet.destroyEntity
import cn.inrhor.questengine.common.database.data.PlayerData
import cn.inrhor.questengine.utlis.location.LocationTool
import org.bukkit.entity.Player
import taboolib.common.util.Location
import taboolib.module.effect.ParticleSpawner
import taboolib.platform.util.toBukkitLocation

/**
 * 此功能不稳定，暂时停用
 */
class PacketRemoveSpawner(val player: Player, val playerData: PlayerData): ParticleSpawner {
    override fun spawn(location: Location) {
        /*val bukkitLoc = location.toBukkitLocation()
        playerData.dataPacket.forEach { (id, data) ->
            for (i in data.count() -1 downTo 0) {
                val it = data[i]
                if (it.packetID == id) {
                    if (LocationTool.inLoc(bukkitLoc, it.location, 0.0)) {
                        destroyEntity(player, it.entityID)
                        data.remove(it)
                    }
                }
            }
        }*/
    }
}