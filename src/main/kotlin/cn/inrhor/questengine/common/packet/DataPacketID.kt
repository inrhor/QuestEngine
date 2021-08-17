package cn.inrhor.questengine.common.packet

import cn.inrhor.questengine.common.database.data.PacketData
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

/**
 * 生成数据包整数型ID和控制数量
 */
class DataPacketID(
    val player: Player,
    val packetID: String,
    var number: Int,
    var location: Location,
    private val dataPackets: MutableList<PacketData>) {

    constructor(player: Player, packetID: String, number: Int, location: Location):
            this(player, packetID, number, location, mutableListOf())

    private var hasAmount = 0

    fun canGet(): Boolean = number > hasAmount

    init {
        for (n in 0..number) {
            val entityID = UUID.randomUUID().hashCode()
            dataPackets.add(PacketData(packetID, entityID, location))
        }
        PacketManager.addDataPacket(player, packetID, dataPackets)
    }

    fun getEntityID(): Int {
        val dataPacket = dataPackets[hasAmount]
        hasAmount++
        return dataPacket.entityID
    }

}