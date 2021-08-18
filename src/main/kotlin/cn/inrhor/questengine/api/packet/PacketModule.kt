package cn.inrhor.questengine.api.packet

class PacketModule(val packedID: String, var viewer: String) {

    lateinit var entityType: String

    var mate: MutableList<String> = mutableListOf()

    var action: ActionModule? = null

}