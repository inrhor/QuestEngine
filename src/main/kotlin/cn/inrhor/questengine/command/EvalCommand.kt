package cn.inrhor.questengine.command

import cn.inrhor.questengine.script.kether.runEval
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.*

internal object EvalCommand {

    val eval = subCommand {
        dynamic {
            execute<ProxyPlayer> { sender, _, argument ->
                runEval(sender.cast(), argument)
            }
        }
    }

}