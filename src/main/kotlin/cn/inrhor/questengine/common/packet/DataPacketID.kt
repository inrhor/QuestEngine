package cn.inrhor.questengine.common.packet

import cn.inrhor.questengine.api.packet.PacketActionType
import cn.inrhor.questengine.api.packet.PacketModule
import cn.inrhor.questengine.common.database.data.PacketData
import cn.inrhor.questengine.common.packet.action.ClickActionData
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

/**
 * 生成数据包整数型ID和控制数量
 */
class DataPacketID(
    val player: Player,
    val packetModule: PacketModule,
    var number: Int,
    var location: Location,
    private val dataPackets: MutableList<PacketData>) {

    constructor(player: Player, packetModule: PacketModule, number: Int, location: Location):
            this(player, packetModule, number, location, mutableListOf())

    private var hasAmount = 0

    fun canGet(): Boolean = number > hasAmount

    init {
        for (n in 0..number) {
            val entityID = UUID.randomUUID().hashCode()
            val packetData = PacketData(packetModule, entityID, location)
            val action = packetModule.action
            if (action?.type == PacketActionType.COLLECTION) {
                val clickAction = packetData.clickAction
                action.set.forEach {
                    val i = it.lowercase()
                    when {
                        i.startsWith("clickcount") -> {
                            clickAction.needClickCount = i.split(" ")[1].toInt()
                        }
                        i.startsWith("addclickcount only") -> {
                            clickAction.passOnly.add(it) // 借助Kether判定
                        }
                        i.startsWith("addclickcount add") -> {
                            clickAction.passAdd.add(it) // 借助Kether判定
                        }
                    }
                }
            }
            dataPackets.add(packetData)
        }
        PacketManager.addDataPacket(player, packetModule.packedID, dataPackets)
    }

    fun getEntityID(): Int {
        val dataPacket = dataPackets[hasAmount]
        hasAmount++
        return dataPacket.entityID
    }

}