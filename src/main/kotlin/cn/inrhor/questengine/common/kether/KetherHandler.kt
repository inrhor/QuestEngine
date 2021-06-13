package cn.inrhor.questengine.common.kether

import cn.inrhor.questengine.common.dialog.optional.holo.animation.text.type.TextWrite
import cn.inrhor.questengine.common.kether.expand.KetherFixedLocation
import cn.inrhor.questengine.utlis.location.FixedLocation
import cn.inrhor.questengine.common.kether.expand.KetherTextWrite
import cn.inrhor.questengine.common.kether.expand.KetherIItemNormal
import io.izzel.taboolib.kotlin.kether.Kether
import io.izzel.taboolib.kotlin.kether.KetherShell
import io.izzel.taboolib.kotlin.kether.common.api.QuestActionParser
import io.izzel.taboolib.module.inject.TFunction
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit

object KetherHandler {

    @TFunction.Init
    fun init() {
        val addAction: (QuestActionParser, String) -> Unit = { parser, name ->
            Kether.addAction(name, parser, "QuestEngine")
        }

        addAction(KetherIItemNormal.parser(), "itemNormal")
        addAction(KetherFixedLocation.parser(), "fixedLocation")
        addAction(KetherTextWrite.parser(), "iHoloWrite")
    }

    fun eval(player: Player, script: String): Any? {
        return KetherShell.eval(script, namespace = listOf("QuestEngine")) {
            this.sender = player
        }.get(1, TimeUnit.SECONDS)
    }

    fun eval(player: Player, script: MutableList<String>): Any? {
        return KetherShell.eval(script, namespace = listOf("QuestEngine")) {
            this.sender = player
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

    fun evalFixedLoc(script: String): FixedLocation {
        return eval(script) as FixedLocation
    }
}