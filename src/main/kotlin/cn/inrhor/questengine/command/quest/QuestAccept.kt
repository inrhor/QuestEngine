package cn.inrhor.questengine.command.quest

import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.utlis.public.MsgUtil
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class QuestAccept {

   fun onCommand(sender: CommandSender, command: Command, label : String, args: Array<out String>) {
       MsgUtil.send("hello")
        val questID = args[1]
       QuestManager.questMap.forEach { (t, u) -> MsgUtil.send("t u  $t   $u") }
        if (!QuestManager.questMap.containsKey(questID)) {
            MsgUtil.send("test")
            return
        }

        val player = Bukkit.getPlayer(args[2]) ?: return

        QuestManager.acceptQuest(player, questID)

        return
    }

}