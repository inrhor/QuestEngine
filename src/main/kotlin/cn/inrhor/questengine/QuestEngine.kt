package cn.inrhor.questengine

import cn.inrhor.questengine.loader.PluginLoader
import io.izzel.taboolib.loader.Plugin
import io.izzel.taboolib.module.config.TConfig
import io.izzel.taboolib.module.inject.TInject

object QuestEngine : Plugin() {
    @TInject(locale = "setting.lang")
    lateinit var config : TConfig

    @TInject(state = TInject.State.STARTING, init = "init")
    lateinit var loader : PluginLoader
}