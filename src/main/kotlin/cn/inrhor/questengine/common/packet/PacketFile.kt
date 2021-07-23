package cn.inrhor.questengine.common.packet

import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.configuration.ConfigurationSection
import java.util.*

object PacketFile {

    fun init(config: ConfigurationSection) {
        val packetID = config.name
        val hook = config.getString("hook")?: return run {
            TLocale.sendToConsole("PACKET.ERROR_FILE", config.name)
        }
        val viewer = config.getString("viewer")?: "all"
        val packerModule = PacketModule(packetID, hook, viewer)
        val entityID = PacketManager.generate(packetID, "entity")
        packerModule.entityID = entityID
        if (!hook.lowercase(Locale.getDefault()).startsWith("normal ")) {
            packerModule.entityType = config.getString("entityType")?: "ARMOR_STAND"
            val mate = config.getStringList("mate")
            packerModule.mate = mate
//            packerModule.itemEntityID = PacketManager.returnItemEntityID(packetID, mate)
        }
        PacketManager.register(packetID, packerModule)
    }

}