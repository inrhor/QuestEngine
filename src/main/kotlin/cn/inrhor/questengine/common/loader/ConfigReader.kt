package cn.inrhor.questengine.common.loader

import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

object ConfigReader {

    @Config("record/chat.yml", true, true)
    lateinit var recordChat: Configuration
        private set

}