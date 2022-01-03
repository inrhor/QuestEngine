package cn.inrhor.questengine.command

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.item.ItemManager
import org.bukkit.entity.Player
import taboolib.common.platform.*
import taboolib.common.platform.command.*
import taboolib.library.xseries.XItemStack
import taboolib.module.configuration.Configuration
import taboolib.module.lang.sendLang
import taboolib.platform.util.isAir
import java.io.File

object ItemCommand {

    val item = subCommand {
        dynamic {
            suggestion<ProxyCommandSender> { _, _ ->
                listOf("save")
            }
            dynamic {
                execute<ProxyCommandSender> { sender, _, argument ->
                    val args = argument.split(" ")
                    val itemID = args[0]
                    if (ItemManager.exist(itemID)) {
                        sender.sendLang("ITEM-EXIST-ID", itemID)
                        return@execute
                    }
                    val player: Player = sender.cast()
                    val item = player.inventory.itemInMainHand
                    if (item.isAir()) {
                        sender.sendLang("ITEM-NOT-SAVE", itemID)
                        return@execute
                    }
                    val file = File(QuestEngine.plugin.dataFolder, "space/item/item.yml")
                    if (!file.exists()) {
                        file.createNewFile()
                    }
                    val yaml = Configuration.loadFromFile(file)
                    yaml.createSection(itemID)
                    XItemStack.serialize(item, yaml.getConfigurationSection(itemID)!!)
                    yaml.saveToFile(file)
                    sender.sendLang("COMMAND-ITEM-SAVE", itemID)
                }
            }
        }
    }
}