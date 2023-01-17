
package cn.inrhor.questengine.script.kether

import cn.inrhor.questengine.utlis.variableReader
import org.bukkit.entity.Player
import taboolib.common.platform.function.*
import taboolib.common5.Coerce
import taboolib.module.chat.colored
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.printKetherErrorMessage
import taboolib.platform.compat.replacePlaceholder
import taboolib.platform.util.asLangText

fun Player.eval(script: String, variable: (ScriptContext) -> Unit, get: (Any?) -> Any, def: Any): Any {
    return KetherShell.eval(script, sender = adaptPlayer(this), namespace = listOf("QuestEngine", "adyeshach")) {
        variable(this)
    }.thenApply {
        get(it)
    }.getNow(def)
}

fun runEval(player: Player, script: String): Boolean {
    if (script.isEmpty()) return true
    return try {
        KetherShell.eval(script, sender = adaptPlayer(player), namespace = listOf("QuestEngine", "adyeshach")).thenApply {
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
        KetherShell.eval(script, sender = adaptPlayer(player), namespace = listOf("QuestEngine", "adyeshach")).thenApply {
            Coerce.toBoolean(it)
        }.getNow(true)
    } catch (ex: Throwable) {
        console().sendMessage("&cError Script: $script".colored())
        ex.printKetherErrorMessage()
        false
    }
}

fun runEvalSet(players: Set<Player>, script: String, variable: (ScriptContext) -> Unit): Boolean {
    if (script.isEmpty()) return true
    players.forEach {
        if (!(it.eval(script, variable, { a->
            Coerce.toBoolean(a)
            }, true) as Boolean)) return false
    }
    return true
}

fun Player.evalStringList(script: List<String>, variable: (ScriptContext) -> Unit): List<String> {
    val list = mutableListOf<String>()
    script.forEach {
        list.add(evalString(it) { a ->
            variable(a)
        })
    }
    return list
}

fun Player.evalString(script: String, variable: (ScriptContext) -> Unit): String {
    var text = script
    script.variableReader("[[", "]]").forEach { e ->
        text = text.replace("[[$e]]", eval(e, {
            variable(it
            )}, {
            Coerce.toString(it)
        }, script).toString())
    }
    return text.replacePlaceholder(this).colored()
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
        KetherShell.eval(script, sender = adaptPlayer(player), namespace = listOf("QuestEngine", "adyeshach")).thenApply {
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
        KetherShell.eval(script, sender = adaptPlayer(player), namespace = listOf("QuestEngine", "adyeshach")).thenApply {
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