package cn.inrhor.questengine.server

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.database.type.DatabaseManager
import cn.inrhor.questengine.common.dialog.DialogManager
import cn.inrhor.questengine.common.item.ItemManager
import cn.inrhor.questengine.common.packet.PacketManager
import cn.inrhor.questengine.common.quest.QuestFile
import cn.inrhor.questengine.utlis.UtilString
import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.*
import taboolib.module.lang.sendLang
import taboolib.module.nms.MinecraftVersion
import java.io.File
import kotlin.system.measureTimeMillis

object PluginLoader {

    private var reloading = false

    @Awake(LifeCycle.ENABLE)
    fun init() {
        val version = MinecraftVersion.major
        ConsoleMsg.logoSend(version)
        if (version <= 3) {
            disablePlugin()
            return
        }
        doLoad()
        DatabaseManager.init()
    }

    @Awake(LifeCycle.DISABLE)
    fun cancel() {
        Bukkit.getScheduler().cancelTasks(QuestEngine.plugin)
        clearMap()
    }

    private fun doLoad() {
        UtilString.updateLang().forEach {
            UpdateYaml.run("lang/$it.yml")
        }
        submit(async = true) {
            val timeCost = measureTimeMillis {
                ItemManager.loadItem()
                DialogManager.loadDialog()
                PacketManager.loadPacket()
                QuestFile.loadDialog()
                val teamChat = File(QuestEngine.plugin.dataFolder, "team/chat.yml")
                if (!teamChat.exists()) {
                    QuestEngine.resource.releaseResourceFile("team/chat.yml", true)
                }
                UpdateYaml.run("team/chat.yml")
            }
            console().sendLang("LOADER-TIME_COST", UtilString.pluginTag, timeCost)
        }
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