package cn.inrhor.questengine

import taboolib.common.platform.Plugin
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.platform.BukkitIO
import taboolib.platform.BukkitPlugin

object QuestEngine : Plugin() {
    @Config(migrate = true)
    lateinit var config: Configuration
        private set

    val plugin by lazy {
        BukkitPlugin.getInstance()
    }

    val resource by lazy {
        BukkitIO()
    }

}