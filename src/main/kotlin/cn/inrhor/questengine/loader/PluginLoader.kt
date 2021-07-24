package cn.inrhor.questengine.loader

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.database.type.DatabaseManager
import cn.inrhor.questengine.common.dialog.DialogManager
import cn.inrhor.questengine.common.item.ItemManager
import cn.inrhor.questengine.common.packet.PacketManager
import cn.inrhor.questengine.common.quest.QuestFile
import cn.inrhor.questengine.utlis.public.UseString
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.Bukkit
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
        UpdateYaml().run(UseString.getLang())
        Bukkit.getScheduler().runTaskAsynchronously(QuestEngine.plugin, Runnable {
            val timeCost = measureTimeMillis {
                ItemManager.loadItem()
                DialogManager.loadDialog()
                PacketManager.loadPacket()
                QuestFile.loadDialog()
            }
            TLocale.sendToConsole("LOADER.TIME_COST", UseString.pluginTag, timeCost)
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