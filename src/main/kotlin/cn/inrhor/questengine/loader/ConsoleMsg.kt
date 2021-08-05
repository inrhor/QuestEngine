package cn.inrhor.questengine.loader

import cn.inrhor.questengine.QuestEngine
import taboolib.common.platform.console
import taboolib.common.platform.info
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang

object ConsoleMsg {

    fun logoSend() {
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

        console().sendLang(
            "LOADER-INFO",
            pluginCon.name,
            pluginCon.version
        )
    }

}