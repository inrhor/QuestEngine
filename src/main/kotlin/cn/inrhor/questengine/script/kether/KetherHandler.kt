package cn.inrhor.questengine.script.kether

import cn.inrhor.questengine.common.dialog.animation.text.type.TextWrite
import cn.inrhor.questengine.utlis.location.FixedLocation
import cn.inrhor.questengine.utlis.location.FixedHoloHitBox
import org.bukkit.entity.Player
import taboolib.common.platform.adaptPlayer
import taboolib.module.kether.KetherShell

object KetherHandler {

    fun eval(player: Player, script: String): Any {
        return KetherShell.eval(script, namespace = listOf("QuestEngine")) {
            sender = adaptPlayer(player)
        }
    }

    fun eval(player: Player, script: MutableList<String>): Any {
        return KetherShell.eval(script, namespace = listOf("QuestEngine")) {
            sender = adaptPlayer(player)
        }
    }

    fun eval(script: String): Any {
        return KetherShell.eval(script, namespace = listOf("QuestEngine"))
    }

    fun eval(script: MutableList<String>): Any {
        return KetherShell.eval(script, namespace = listOf("QuestEngine"))
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

    fun evalFixedLoc(script: String): FixedLocation {
        return eval(script) as FixedLocation
    }

    fun evalHoloHitBox(script: String): FixedHoloHitBox {
        return eval(script) as FixedHoloHitBox
    }
}