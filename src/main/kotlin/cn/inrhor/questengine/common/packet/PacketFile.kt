package cn.inrhor.questengine.common.packet

import cn.inrhor.questengine.api.packet.PacketModule
import taboolib.common.platform.function.*
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.lang.sendLang

object PacketFile {

    fun init(config: ConfigurationSection) {
        val packetID = config.name
        val viewer = config.getString("viewer")?: "all"
        val packerModule = PacketModule(packetID, viewer)
        val entityID = PacketManager.generate(packetID, "entity")
        packerModule.entityID = entityID
        packerModule.entityType = config.getString("entityType")?: "ARMOR_STAND"
        packerModule.mate = config.getStringList("mate")
        packerModule.action = config.getStringList("action")
        PacketManager.register(packetID, packerModule)
    }

}