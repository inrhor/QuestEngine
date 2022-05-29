
package cn.inrhor.questengine.script.kether

import org.bukkit.entity.Player
import taboolib.common.platform.function.*
import taboolib.common5.Coerce
import taboolib.module.chat.colored
import taboolib.module.kether.KetherShell
import taboolib.platform.util.asLangText

fun runEval(player: Player, script: String): Boolean {
    return try {
        KetherShell.eval(script, namespace = listOf("QuestEngine")) {
            sender = adaptPlayer(player)
        }.thenApply {
            Coerce.toBoolean(it)
        }.get()
    } catch (ex: Exception) {
        console().sendMessage("&cError Script: $script".colored())
        info(ex.localizedMessage)
        false
    }
}

fun runEval(player: Player, script: List<String>): Boolean {
    if (script.isEmpty()) return true
    return try {
        KetherShell.eval(script, namespace = listOf("QuestEngine")) {
            sender = adaptPlayer(player)
        }.thenApply {
            Coerce.toBoolean(it)
        }.get()
    } catch (ex: Exception) {
        console().sendMessage("&cError Script: $script".colored())
        info(ex.localizedMessage)
        false
    }
}

fun runEvalSet(players: Set<Player>, script: String): Boolean {
    if (script.isEmpty()) return true
    players.forEach {
        if (!runEval(it, script)) return false
    }
    return true
}

fun testEval(player: Player, script: String): EvalType {
    if (script.isEmpty()) return EvalType.TRUE
    return try {
        KetherShell.eval(script, namespace = listOf("QuestEngine")) {
            sender = adaptPlayer(player)
        }.thenApply {
            Coerce.toBoolean(it).evalType()
        }.getNow(null)
    } catch (ex: Exception) {
        EvalType.ERROR
    }
}

fun feedbackEval(player: Player, script: String): String {
    return try {
        KetherShell.eval(script, namespace = listOf("QuestEngine")) {
            sender = adaptPlayer(player)
        }.thenApply {
            ""
        }.getNow(null)
    } catch (ex: Exception) {
        ex.localizedMessage
    }
}

enum class EvalType {
    TRUE, FALSE, ERROR
}
fun Boolean.evalType() = if (this) EvalType.TRUE else EvalType.FALSE
fun EvalType.lang(player: Player, content: String): String {
    return player.asLangText("$content-$this")
}