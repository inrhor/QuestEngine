package cn.inrhor.questengine.command.quest

import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.Bukkit

class QuestAccept {

   fun onCommand(args: Array<out String>) {
        val questID = args[1]
        if (!QuestManager.questMap.containsKey(questID)) {
            return
        }

        val player = Bukkit.getPlayer(args[2]) ?: return

        QuestManager.acceptQuest(player, questID)

        return
    }

}