package cn.inrhor.questengine.script.kether

import cn.inrhor.questengine.common.dialog.animation.text.type.TextWrite
import cn.inrhor.questengine.utlis.location.ReferHoloHitBox
import cn.inrhor.questengine.utlis.location.ReferLocation
import org.bukkit.entity.Player
import taboolib.common.platform.adaptPlayer
import taboolib.module.kether.KetherShell
import java.util.concurrent.TimeUnit

fun eval(player: Player, script: String): Any? {
    return KetherShell.eval(script, namespace = listOf("QuestEngine")) {
        sender = adaptPlayer(player)
    }.get(1, TimeUnit.SECONDS)
}

fun eval(player: Player, script: MutableList<String>): Any? {
    return KetherShell.eval(script, namespace = listOf("QuestEngine")) {
        sender = adaptPlayer(player)
    }.get(1, TimeUnit.SECONDS)
}

fun eval(script: String): Any? {
    return KetherShell.eval(script, namespace = listOf("QuestEngine"))
        .get(1, TimeUnit.SECONDS)
}

fun eval(script: MutableList<String>): Any? {
    return KetherShell.eval(script, namespace = listOf("QuestEngine"))
        .get(1, TimeUnit.SECONDS)
}

fun evalBoolean(player: Player, script: String): Boolean {
    return eval(player, script) as Boolean
}

fun evalBoolean(player: Player, script: MutableList<String>): Boolean {
    return eval(player, script) as Boolean
}

fun evalBooleanSet(players: MutableSet<Player>, script: MutableList<String>): Boolean {
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

fun evalHoloHitBox(script: String): ReferHoloHitBox {
    return eval(script) as ReferHoloHitBox
}