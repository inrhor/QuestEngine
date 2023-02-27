package cn.inrhor.imipetcore.server

import org.bukkit.Bukkit
import taboolib.module.nms.MinecraftVersion

object ReadManager {

    /**
     * 主版本号
     */
    val major = MinecraftVersion.major

    /**
     * 次版本号
     */
    val minor = MinecraftVersion.minor

    /**
     * 是否为1.17+
     */
    val isUniversal = MinecraftVersion.isUniversal

    val attributePlus by lazy {
        Bukkit.getPluginManager().getPlugin("AttributePlus") != null
    }

    val authMeLoad by lazy {
        Bukkit.getPluginManager().getPlugin("AuthMe") != null
    }

    val protocolLibLoad by lazy {
        Bukkit.getPluginManager().getPlugin("ProtocolLib") != null
                && ConfigRead.nms == "mod"
    }

    val adyeshachLoad by lazy {
        Bukkit.getPluginManager().getPlugin("Adyeshach") != null
    }

    val decentHologramsLoad by lazy {
        Bukkit.getPluginManager().getPlugin("DecentHolograms") != null
    }

    val inveroLoad by lazy {
        Bukkit.getPluginManager().getPlugin("Invero") != null
    }

    val mythicLoad by lazy {
        Bukkit.getPluginManager().getPlugin("MythicMobs") != null
    }

}