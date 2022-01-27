
package cn.inrhor.questengine.script.kether

import org.bukkit.entity.Player
import taboolib.common.platform.function.*
import taboolib.common5.Coerce
import taboolib.module.chat.colored
import taboolib.module.kether.KetherShell

fun runEval(player: Player, script: String): Boolean {
    return try {
        KetherShell.eval(script, namespace = listOf("QuestEngine")) {
            sender = adaptPlayer(player)
        }.thenApply {
            Coerce.toBoolean(it)
        }.get()
    } catch (ex: Exception) {
        console().sendMessage("&cError Script $script".colored())
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
        console().sendMessage("&cError Script $script".colored())
        false
    }
}

fun runEvalSet(players: Set<Player>, script: List<String>): Boolean {
    if (script.isEmpty()) return true
    players.forEach {
        if (!runEval(it, script)) return false
    }
    return true
}

/*
fun runEval(player: Player, script: String): Any? {
    return runEval(player, mutableListOf(script))
}

fun runEval(player: Player, script: List<String>): Any? {
    return try {
        KetherShell.eval(script, namespace = listOf("QuestEngine")) {
            sender = adaptPlayer(player)
        }.get(1, TimeUnit.SECONDS)
    }catch (ex: Exception) {
        console().sendMessage("&cError Kether: &r$script".colored())
    }
}

fun runEval(script: String): Any? {
    return runEval(mutableListOf(script))
}

fun runEval(script: List<String>): Any? {
    return try {
        KetherShell.eval(script, namespace = listOf("QuestEngine"))
            .get(1, TimeUnit.SECONDS)
    }catch (ex: Exception) {
        console().sendMessage("&cError Kether: &r$script".colored())
    }
}

fun evalBoolean(player: Player, script: String): Boolean {
    return evalBoolean(player, mutableListOf(script))
}

fun evalBoolean(player: Player, script: List<String>): Boolean {
    if (script.isEmpty()) return true
    return runEval(player, script) as Boolean
}

fun evalBooleanSet(players: MutableSet<Player>, script: List<String>): Boolean {
    if (script.isEmpty()) return true
    players.forEach{
        if (!(eval(it, script) as Boolean)) return false
    }
    return true
}*/
