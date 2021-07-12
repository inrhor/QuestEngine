package cn.inrhor.questengine.common.packet

import org.bukkit.entity.EntityType

class PacketModule(val packedID: String, var hook: String, var viewer: String) {

    var entityID: Int = 0

    lateinit var entityType: EntityType

    lateinit var mate: MutableList<String>

//    lateinit var itemEntityID: MutableMap<String, Int>

}