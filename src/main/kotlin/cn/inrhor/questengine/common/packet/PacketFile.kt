package cn.inrhor.questengine.common.packet

import cn.inrhor.questengine.api.packet.PacketModule
import taboolib.common.platform.console
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.lang.sendLang

object PacketFile {

    fun init(config: ConfigurationSection) {
        val packetID = config.name
        val hook = config.getString("hook")?: return run {
            console().sendLang("PACKET-ERROR_FILE", config.name)
        }
        val viewer = config.getString("viewer")?: "all"
        val packerModule = PacketModule(packetID, hook, viewer)
        val entityID = PacketManager.generate(packetID, "entity")
        packerModule.entityID = entityID
        if (!hook.lowercase().startsWith("normal ")) {
            packerModule.entityType = config.getString("entityType")?: "ARMOR_STAND"
            val mate = config.getStringList("mate")
            packerModule.mate = mate
//            packerModule.itemEntityID = PacketManager.returnItemEntityID(packetID, mate)
        }
        PacketManager.register(packetID, packerModule)
    }

}