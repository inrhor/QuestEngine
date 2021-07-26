package cn.inrhor.questengine.command.quest

import cn.inrhor.questengine.common.quest.manager.QuestManager
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class InnerQuestAccept {

    fun onCommand(sender: CommandSender, args: Array<out String>) {
        val questID = args[1]
        if (!QuestManager.questMap.containsKey(questID)) {
            return
        }

        val innerQuestID = args[2]

        val player = Bukkit.getPlayer(args[3])?: return run { TLocale.sendTo(sender, "PLAYER_NOT_ONLINE") }
        val uuid = player.uniqueId

        val questData = QuestManager.getQuestData(uuid, questID)?: return run {
            TLocale.sendTo(sender, "QUEST.NULL_QUEST_DATA") }

        QuestManager.acceptInnerQuest(player, questData, innerQuestID, true)

        return
    }

}