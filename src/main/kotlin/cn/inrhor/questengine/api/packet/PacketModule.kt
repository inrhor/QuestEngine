package cn.inrhor.questengine.api.packet

class PacketModule(val packedID: String, var viewer: String) {

//    var entityID: Int = 0

    lateinit var entityType: String

    var mate: MutableList<String> = mutableListOf()

    var action: MutableList<String> = mutableListOf()

}