package cn.inrhor.questengine.common.database.data

import org.bukkit.entity.Player

class DataPacketID(
    val player: Player,
    val packetID: String,
    var number: Int,
    private val entityIDs: MutableList<Int>) {

    constructor(player: Player, packetID: String, number: Int):
            this(player, packetID, number, mutableListOf())

    private var hasAmount = 0

    fun canGet(): Boolean = (number > hasAmount)

    init {
        for (n in 0..number) {
            val entityID = "packet&$packetID&>$n".hashCode()
            entityIDs.add(entityID)
        }
        val pData = DataStorage.getPlayerData(player)
        pData.packetEntitys[packetID] = entityIDs
    }

    fun getEntityID(): Int {
        if (!canGet()) return 0
        val entityID = entityIDs[hasAmount]
        hasAmount++
        return entityID
    }

}