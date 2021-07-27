package cn.inrhor.questengine.command

import cn.inrhor.questengine.loader.PluginLoader
import io.izzel.taboolib.module.command.base.BaseSubCommand
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class ReloadCommand: BaseSubCommand() {
    override fun onCommand(sender: CommandSender, command: Command, label : String, args: Array<out String>) {
        PluginLoader().doReload()
        TLocale.sendTo(sender, "COMMAND.SUCCESSFUL_RELOAD")
        return
    }
}