package cn.inrhor.questengine.common.packet

import org.bukkit.entity.Player
import taboolib.common.util.Location
import taboolib.module.effect.ParticleSpawner

class PacketSpawner(val player: Player, val packetID: String): ParticleSpawner {
    override fun spawn(location: Location) {
        PacketManager.sendPacket(packetID, player, location)
    }
}