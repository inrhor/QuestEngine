package cn.inrhor.questengine.common.loader

import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

object ConfigReader {

    @Config("record/chat.yml", migrate = true, autoReload = true)
    lateinit var recordChat: Configuration
        private set

}