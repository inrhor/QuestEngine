
package cn.inrhor.questengine.script.kether

import org.bukkit.entity.Player
import taboolib.common.platform.function.*
import taboolib.common5.Coerce
import taboolib.module.chat.colored
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage
import taboolib.platform.util.asLangText

fun runEval(player: Player, script: String): Boolean {
    if (script.isEmpty()) return true
    return try {
        KetherShell.eval(script, sender = adaptPlayer(player), namespace = listOf("QuestEngine")).thenApply {
            Coerce.toBoolean(it)
        }.getNow(true)
    } catch (ex: Throwable) {
        console().sendMessage("&cError Script: $script".colored())
        ex.printKetherErrorMessage()
        false
    }
}

fun runEval(player: Player, script: List<String>): Boolean {
    if (script.isEmpty()) return true
    return try {
        KetherShell.eval(script, sender = adaptPlayer(player), namespace = listOf("QuestEngine")).thenApply {
            Coerce.toBoolean(it)
        }.getNow(true)
    } catch (ex: Throwable) {
        console().sendMessage("&cError Script: $script".colored())
        ex.printKetherErrorMessage()
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

fun backContains(player: Player, content: String, back: Boolean = true, eval: Boolean = true): EvalType {
    if (eval) {
        return testEval(player, content)
    }else {
        if (back) return EvalType.TRUE else EvalType.FALSE
    }
    return EvalType.FALSE
}

fun testEval(player: Player, script: String): EvalType {
    if (script.isEmpty()) return EvalType.TRUE
    return try {
        KetherShell.eval(script, sender = adaptPlayer(player), namespace = listOf("QuestEngine")).thenApply {
            Coerce.toBoolean(it).evalType()
        }.getNow(null)
    } catch (ex: Throwable) {
        console().sendMessage("&cError Script: $script".colored())
        ex.printKetherErrorMessage()
        EvalType.ERROR
    }
}

fun feedbackEval(player: Player, script: String): String {
    return try {
        KetherShell.eval(script, sender = adaptPlayer(player), namespace = listOf("QuestEngine")).thenApply {
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