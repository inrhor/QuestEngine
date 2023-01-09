package cn.inrhor.questengine.command

import cn.inrhor.questengine.script.kether.runEval
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*

internal object EvalCommand {

    val eval = subCommand {
        dynamic {
            // 在线玩家
            suggestion<ProxyCommandSender> { _, _ ->
                Bukkit.getOnlinePlayers().map { it.name }
            }
            dynamic {
                execute<ProxyCommandSender> { _, context, argument ->
                    val p = Bukkit.getPlayer(context.argument(-1)) ?: return@execute
                    runEval(p, argument)
                }
            }
        }
    }

}