package cn.inrhor.questengine.command

import cn.inrhor.questengine.loader.PluginLoader
import taboolib.common.platform.*
import taboolib.module.lang.sendLang

object ReloadCommand {

    val reload = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            PluginLoader.doReload()
            sender.sendLang("COMMAND-SUCCESSFUL_RELOAD")
        }
    }
}