package cn.inrhor.questengine.loader

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.dialog.DialogManager
import cn.inrhor.questengine.common.item.ItemManager
import cn.inrhor.questengine.utlis.public.UseString
import org.bukkit.Bukkit

class PluginLoader {

    fun init() {
        UpdateYaml().run(UseString.getLang())
        InfoSend().logoSend()

        Bukkit.getScheduler().runTaskAsynchronously(QuestEngine.plugin, Runnable {
            ItemManager().loadDialog()
            DialogManager().loadDialog()
        })
    }

}