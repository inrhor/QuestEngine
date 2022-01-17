package cn.inrhor.questengine.common.packet

import cn.inrhor.questengine.api.packet.ActionModule
import cn.inrhor.questengine.api.packet.PacketModule
import cn.inrhor.questengine.api.packet.toPacketAction
import taboolib.library.configuration.ConfigurationSection

object PacketFile {

    fun init(config: ConfigurationSection) {
        val packetID = config.name
        val viewer = config.getString("viewer")?: "all"
        val packerModule = PacketModule(packetID, viewer)
        packerModule.entityType = config.getString("entityType")?: "ARMOR_STAND"
        packerModule.mate = config.getStringList("mate")
        if (config.contains("action.type")) {
            val action = ActionModule(
                config.getString("action.type")!!.toPacketAction(),
                config.getStringList("action.set"),
                config.getStringList("action.trigger"),
                config.getStringList("action.pass"),
                config.getBoolean("action.ratio.enable"))
            packerModule.action = action
        }
        PacketManager.register(packetID, packerModule)
    }

}