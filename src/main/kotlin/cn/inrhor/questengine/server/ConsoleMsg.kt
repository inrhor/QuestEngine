package cn.inrhor.questengine.server

import cn.inrhor.questengine.QuestEngine
import taboolib.common.platform.function.*
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang

object ConsoleMsg {

    fun logoSend(version: Int) {
        val logo = listOf(
            "&b   ____                  _   ______             _            ",
            "&b  / __ \\                | | |  ____|           (_)           ",
            "&b | |  | |_   _  ___  ___| |_| |__   _ __   __ _ _ _ __   ___ ",
            "&b | |  | | | | |/ _ \\/ __| __|  __| | '_ \\ / _` | | '_ \\ / _ \\",
            "&b | |__| | |_| |  __/\\__ \\ |_| |____| | | | (_| | | | | |  __/",
            "&b  \\___\\_\\\\__,_|\\___||___/\\__|______|_| |_|\\__, |_|_| |_|\\___|",
            "&b                                           __/ |             ",
            "&b                                          |___/              "
        )
        for (s in logo) {
            console().sendMessage(s.colored())
        }

        val pluginCon = QuestEngine.plugin.description

        val state = if (version > 3) "&a√" else "&c×"

        console().sendLang(
            "LOADER-INFO",
            pluginCon.name,
            pluginCon.version,
            "&8[ $state &8]"
        )
    }

}