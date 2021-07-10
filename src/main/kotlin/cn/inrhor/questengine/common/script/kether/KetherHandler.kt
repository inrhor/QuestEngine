package cn.inrhor.questengine.common.script.kether

import cn.inrhor.questengine.common.dialog.animation.text.type.TextWrite
import cn.inrhor.questengine.common.script.kether.expand.KetherFixedLocation
import cn.inrhor.questengine.common.script.kether.expand.KetherHitBox
import cn.inrhor.questengine.utlis.location.FixedLocation
import cn.inrhor.questengine.common.script.kether.expand.KetherTextWrite
import cn.inrhor.questengine.common.script.kether.expand.KetherIItemNormal
import cn.inrhor.questengine.utlis.location.FixedHoloHitBox
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
        addAction(KetherFixedLocation.parser(), "initLoc")
        addAction(KetherFixedLocation.parser(), "addLoc")
        addAction(KetherTextWrite.parser(), "textWrite")
        addAction(KetherHitBox.parser(), "hitBox")
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

    fun evalHoloHitBox(script: String): FixedHoloHitBox {
        return eval(script) as FixedHoloHitBox
    }
}