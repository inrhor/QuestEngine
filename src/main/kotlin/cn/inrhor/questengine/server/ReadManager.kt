package cn.inrhor.questengine.server

import org.bukkit.Bukkit

object ReadManager {

    val authMeLoad by lazy {
        Bukkit.getPluginManager().getPlugin("AuthMe") != null
    }

    val inveroLoad by lazy {
        Bukkit.getPluginManager().getPlugin("Invero") != null
    }

}