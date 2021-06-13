package cn.inrhor.questengine.loader

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.dialog.DialogManager
import cn.inrhor.questengine.common.item.ItemManager
import cn.inrhor.questengine.utlis.public.UseString
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.Bukkit
import kotlin.system.measureTimeMillis

class PluginLoader {

    fun init() {
        UpdateYaml().run(UseString.getLang())
        InfoSend().logoSend()
        Bukkit.getScheduler().runTaskAsynchronously(QuestEngine.plugin, Runnable {
            val timeCost = measureTimeMillis {
                ItemManager().loadDialog()
                DialogManager().loadDialog()
            }
            TLocale.sendToConsole("LOADER.TIME_COST", UseString.pluginTag, timeCost)
        })
    }

    private var reloading = false

    fun cancel() {
        Bukkit.getScheduler().cancelTasks(QuestEngine.plugin)
        clearMap()
        if (!reloading) {
            // 保存数据
        }
    }

    fun doReload() {
        if (reloading) {
            throw RuntimeException("reloading")
        }
        reloading = true
        clearMap()
        reloading = false
    }

    fun clearMap() {
        DialogManager().clearMap()
        ItemManager().clearMap()
    }

}