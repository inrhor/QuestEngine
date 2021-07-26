package cn.inrhor.questengine.command.quest

import cn.inrhor.questengine.common.quest.manager.QuestManager
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class QuestQuit {

    fun onCommand(sender: CommandSender, args: Array<out String>) {

        val questID = args[1]

        val player = Bukkit.getPlayer(args[2]) ?: return run { TLocale.sendTo(sender, "PLAYER_NOT_ONLINE") }

        QuestManager.quitQuest(player, questID)

        return
    }

}