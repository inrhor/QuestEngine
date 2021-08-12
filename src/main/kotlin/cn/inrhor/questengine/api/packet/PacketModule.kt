package cn.inrhor.questengine.api.packet

class PacketModule(val packedID: String, var hook: String, var viewer: String) {

    var entityID: Int = 0

    lateinit var entityType: String

    lateinit var mate: MutableList<String>

}