package cn.inrhor.questengine.common.packet

object PacketIDManager {

    val entityIDMap = mutableMapOf<String, Int>()

    /**
     * type > packet
     */
    fun generate(packetID: String, type: String): Int {
        return "packet-$packetID-$type".hashCode()
    }

    fun addID(packetID: String, entityID: Int) {
        entityIDMap[packetID] = entityID
    }

    fun removeID(packetID: String) {
        entityIDMap.remove(packetID)
    }

}