package cn.inrhor.questengine.common.packet

import org.bukkit.configuration.ConfigurationSection
import java.util.*

object PacketFile {

    fun init(config: ConfigurationSection) {
        if (!config.contains("hook")) {
            return
        }
        val packetID = config.name
        val hook = config.getString("hook")?: return
        val viewer = config.getString("viewer")?: "all"
        val packerModule = PacketModule(packetID, hook, viewer)
        val entityID = PacketManager.generate(packetID, "entity")
        packerModule.entityID = entityID
        if (!hook.lowercase(Locale.getDefault()).startsWith("normal ")) {
            packerModule.entityType = PacketManager.returnEntityType(config.getString("entityType")?: "armor_stand")
            val mate = config.getStringList("mate")
            packerModule.mate = mate
//            packerModule.itemEntityID = PacketManager.returnItemEntityID(packetID, mate)
        }
        PacketManager.register(packetID, packerModule)
    }

}