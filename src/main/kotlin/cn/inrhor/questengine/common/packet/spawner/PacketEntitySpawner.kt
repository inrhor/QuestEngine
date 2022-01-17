package cn.inrhor.questengine.common.packet.spawner

import cn.inrhor.questengine.common.packet.DataPacketID
import cn.inrhor.questengine.common.packet.PacketManager
import org.bukkit.entity.Player
import taboolib.common.util.Location
import taboolib.module.effect.ParticleSpawner
import taboolib.platform.util.toBukkitLocation

class PacketEntitySpawner(val player: Player, val dataPacketID: DataPacketID): ParticleSpawner {
    override fun spawn(location: Location) {
        val bukkitLoc = location.toBukkitLocation()
        if (!dataPacketID.canGet()) return
        PacketManager.sendThisPacket(dataPacketID.packetModule, dataPacketID.getEntityID(), player, bukkitLoc)
        dataPacketID.location = bukkitLoc
    }
}