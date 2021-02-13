package cn.inrhor.questengine.common.kether

import cn.inrhor.questengine.common.dialog.location.FixedLocation
import cn.inrhor.questengine.common.kether.expand.KetherFixedLocation
import io.izzel.taboolib.kotlin.kether.Kether
import io.izzel.taboolib.kotlin.kether.KetherShell
import io.izzel.taboolib.kotlin.kether.common.api.QuestActionParser
import io.izzel.taboolib.kotlin.kether.common.util.LocalizedException
import io.izzel.taboolib.module.inject.TFunction
import org.bukkit.entity.Player

object KetherHandler {

    /*@TFunction.Init
    fun init() {
        val addAction: (QuestActionParser, String) -> Unit = { parser, name ->
            Kether.addAction(name, parser, "QuestEngine")
        }

    }*/

    fun eval(player: Player, script: String): Any? {
        return KetherShell.eval(script, namespace = listOf("QuestEngine")) {
            this.sender = player
        }.get()
    }

    fun eval(script: String): Any? {
        return KetherShell.eval(script, namespace = listOf("QuestEngine")).get()
    }

    fun evalBoolean(player: Player, script: String): Boolean {
        return eval(player, script) as Boolean
    }

    fun evalFixedLoc(script: String): FixedLocation {
        return eval(script) as FixedLocation
    }
}