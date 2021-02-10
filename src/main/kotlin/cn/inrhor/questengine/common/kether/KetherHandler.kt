package cn.inrhor.questengine.common.kether

import cn.inrhor.questengine.common.kether.expand.KetherMoney
import io.izzel.taboolib.kotlin.kether.Kether
import io.izzel.taboolib.kotlin.kether.KetherShell
import io.izzel.taboolib.kotlin.kether.common.api.QuestActionParser
import io.izzel.taboolib.module.inject.TFunction
import org.bukkit.entity.Player

object KetherHandler {

    @TFunction.Init
    fun init() {
        val addAction: (QuestActionParser, String) -> Unit = { parser, name ->
            Kether.addAction(name, parser, "questEngine")
        }

        addAction(KetherMoney.parser(), "money")
    }

    fun checkBoolean(player: Player, script: String): Boolean {
        return KetherShell.eval(script) {
            this.sender = player
        } as Boolean
    }

}