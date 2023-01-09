package cn.inrhor.questengine.common.listener.base

import cn.inrhor.questengine.api.manager.DataManager.existQuestData
import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.manager.QuestManager.acceptQuest
import cn.inrhor.questengine.server.PluginLoader
import fr.xephi.authme.events.LoginEvent
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.*
import taboolib.common.platform.function.*

object JoinQuit {

    @SubscribeEvent
    fun onPlayerJoin(ev: PlayerJoinEvent) {
        if (PluginLoader.authme) return
        autoAccept(ev.player)
    }

    @SubscribeEvent(bind = "fr.xephi.authme.events.LoginEvent")
    fun login(op: OptionalEvent) {
        if (PluginLoader.authme) {
            val ev = op.get<LoginEvent>()
            autoAccept(ev.player)
        }
    }

    fun autoAccept(p: Player) {
        submit(async = true, delay = 20L) {
            QuestManager.autoQuestMap.keys.forEach {
                if (!p.existQuestData(it)) {
                    p.acceptQuest(it)
                }
            }
        }
    }

    @SubscribeEvent
    fun onPlayerQuit(ev: PlayerQuitEvent) {
        val uuid = ev.player.uniqueId
        val pData = uuid.getPlayerData()
        val tData = pData.teamData?: return
        if (TeamManager.isLeader(uuid, tData)) {
            tData.delTeam()
        }else {
            TeamManager.removeMember(uuid, tData)
        }
    }

}