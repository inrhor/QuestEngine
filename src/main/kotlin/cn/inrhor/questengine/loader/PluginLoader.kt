package cn.inrhor.questengine.loader

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.database.type.DatabaseManager
import cn.inrhor.questengine.common.dialog.DialogManager
import cn.inrhor.questengine.common.item.ItemManager
import cn.inrhor.questengine.common.packet.PacketManager
import cn.inrhor.questengine.common.quest.QuestFile
import cn.inrhor.questengine.utlis.public.UtilString
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.Files
import org.bukkit.Bukkit
import java.io.File
import kotlin.system.measureTimeMillis

class PluginLoader {

    fun init() {
        Info.logoSend()
        doLoad()
    }

    private var reloading = false

    fun cancel() {
        Bukkit.getScheduler().cancelTasks(QuestEngine.plugin)
        clearMap()
    }

    private fun doLoad() {
        UpdateYaml.run("lang/"+UtilString.getLang()+".yml")
        Bukkit.getScheduler().runTaskAsynchronously(QuestEngine.plugin, Runnable {
            val timeCost = measureTimeMillis {
                ItemManager.loadItem()
                DialogManager.loadDialog()
                PacketManager.loadPacket()
                QuestFile.loadDialog()
                val teamChat = File(QuestEngine.plugin.dataFolder, "team/chat.yml")
                if (!teamChat.exists()) {
                    Files.releaseResource(QuestEngine.plugin, "team/chat.yml", true)
                }
            }
            TLocale.sendToConsole("LOADER.TIME_COST", UtilString.pluginTag, timeCost)
        })
        DatabaseManager.init()
    }

    fun doReload() {
        if (reloading) {
            throw RuntimeException("reloading")
        }
        reloading = true
        clearMap()
        doLoad()
        reloading = false
    }

    private fun clearMap() {
        DialogManager.clearMap()
        ItemManager.clearMap()
    }

}