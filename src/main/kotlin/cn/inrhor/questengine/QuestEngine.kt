package cn.inrhor.questengine

import taboolib.common.platform.Plugin
import taboolib.module.configuration.Config
import taboolib.module.configuration.SecuredFile
import taboolib.platform.BukkitIO
import taboolib.platform.BukkitPlugin

object QuestEngine : Plugin() {
    @Config(migrate = true)
    lateinit var config: SecuredFile
        private set

    val plugin by lazy {
        BukkitPlugin.getInstance()
    }

    val resource by lazy {
        BukkitIO()
    }

}