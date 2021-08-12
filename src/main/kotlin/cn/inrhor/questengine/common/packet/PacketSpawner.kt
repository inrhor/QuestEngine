package cn.inrhor.questengine.common.packet

import cn.inrhor.questengine.common.database.data.DataPacketID
import org.bukkit.entity.Player
import taboolib.common.util.Location
import taboolib.module.effect.ParticleSpawner
import taboolib.platform.util.toBukkitLocation

class PacketSpawner(val player: Player, val dataPacketID: DataPacketID): ParticleSpawner {
    override fun spawn(location: Location) {
        PacketManager.sendThisPacket(
            dataPacketID.packetID, dataPacketID.getEntityID(),
            player, location.toBukkitLocation())
    }
}