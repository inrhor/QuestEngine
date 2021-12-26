package cn.inrhor.questengine.script.kether

import cn.inrhor.questengine.common.dialog.animation.text.type.TextWrite
import cn.inrhor.questengine.common.dialog.theme.hologram.core.HoloHitBox
import cn.inrhor.questengine.utlis.location.ReferLocation
import org.bukkit.entity.Player
import taboolib.common.platform.function.*
import taboolib.module.chat.colored
import taboolib.module.kether.KetherShell
import java.util.concurrent.TimeUnit

fun eval(player: Player, script: String): Any? {
    return eval(player, mutableListOf(script))
}

fun eval(player: Player, script: MutableList<String>): Any? {
    return try {
        KetherShell.eval(script, namespace = listOf("QuestEngine")) {
            sender = adaptPlayer(player)
        }.get(1, TimeUnit.SECONDS)
    }catch (ex: Exception) {
        console().sendMessage("&cError Kether: &r$script".colored())
    }
}

fun eval(script: String): Any? {
    return eval(mutableListOf(script))
}

fun eval(script: MutableList<String>): Any? {
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

fun evalBoolean(player: Player, script: MutableList<String>): Boolean {
    if (script.isEmpty()) return true
    return eval(player, script) as Boolean
}

fun evalBooleanSet(players: MutableSet<Player>, script: MutableList<String>): Boolean {
    if (script.isEmpty()) return true
    players.forEach{
        if (!(eval(it, script) as Boolean)) return false
    }
    return true
}

fun evalTextWrite(script: String): TextWrite {
    return eval(script) as TextWrite
}

fun evalReferLoc(script: String): ReferLocation {
    return eval(script) as ReferLocation
}

fun evalHoloHitBox(script: String): HoloHitBox {
    return eval(script) as HoloHitBox
}