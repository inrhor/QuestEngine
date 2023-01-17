package cn.inrhor.questengine.api.manager

import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.database.data.InputData
import org.bukkit.entity.Player
import taboolib.platform.util.sendLang

object InputManager {

    fun Player.inputChat(lang: String, vararg value: String) {
        getPlayerData().let {
            it.input = InputData(*value)
            sendLang(lang)
        }
    }

}