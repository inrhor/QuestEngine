package cn.inrhor.questengine.command

import cn.inrhor.questengine.server.PluginLoader
import taboolib.common.platform.*
import taboolib.common.platform.command.*
import taboolib.module.lang.sendLang

object ReloadCommand {

    val reload = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            PluginLoader.unloadTask()
            sender.sendLang("COMMAND-SUCCESSFUL_RELOAD")
            PluginLoader.loadTask(false)
        }
    }
}