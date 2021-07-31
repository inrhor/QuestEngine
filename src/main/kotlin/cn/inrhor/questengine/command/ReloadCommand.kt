package cn.inrhor.questengine.command

import cn.inrhor.questengine.loader.PluginLoader
import taboolib.common.platform.CommandBody
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.subCommand
import taboolib.module.lang.sendLang

object ReloadCommand {

    @CommandBody
    val reload = subCommand {
        literal("reload") {
            dynamic {
                execute<ProxyCommandSender> { sender, _, _ ->
                    PluginLoader().doReload()
                    sender.sendLang("COMMAND.SUCCESSFUL_RELOAD")
                }
            }
        }
    }
}