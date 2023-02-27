package cn.inrhor.questengine.common.hook.protocol

import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.KetherShell
import taboolib.platform.compat.PlaceholderExpansion

object HookPlaceholderAPI: PlaceholderExpansion {

    override val identifier = "qen"

    override fun onPlaceholderRequest(player: Player?, args: String): String {
        if (player == null) return "Null Player"
        return try {
            KetherShell.eval(args,  namespace = listOf("QuestEngine"), sender = adaptPlayer(player)).getNow("<TIMEOUT>").toString()
        }catch (ex: Exception) {
            ex.localizedMessage
            "NULL"
        }
    }

}