package cn.inrhor.questengine.common.kether

import io.izzel.taboolib.kotlin.kether.KetherShell
import org.bukkit.entity.Player

object KetherHandler {

    /*@TFunction.Init
    fun init() {
        val addAction: (QuestActionParser, String) -> Unit = { parser, name ->
            Kether.addAction(name, parser, "QuestEngine")
        }
    }*/

    fun checkBoolean(player: Player, script: String): Boolean {
        return KetherShell.eval(script) {
            this.sender = player
        } as Boolean
    }

    fun getString(player: Player, script: String): String {
        return KetherShell.eval(script) {
            this.sender = player
        } as String
    }

}