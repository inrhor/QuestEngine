package cn.inrhor.questengine.common.packet

class PacketModule(val packedID: String, var hook: String, var viewer: String) {

    var entityID: Int = 0

    lateinit var entityType: String

    lateinit var mate: MutableList<String>

//    lateinit var itemEntityID: MutableMap<String, Int>

}