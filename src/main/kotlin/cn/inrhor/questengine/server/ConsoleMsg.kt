package cn.inrhor.questengine.server

import cn.inrhor.questengine.QuestEngine
import taboolib.common.platform.function.*
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang

object ConsoleMsg {

    fun logo(color: String) {
        console().sendMessage("§$color\n" +
                "  _____                        _______             _             \n" +
                " / ___ \\                  _   (_______)           (_)            \n" +
                "| |   | |_   _  ____  ___| |_  _____   ____   ____ _ ____   ____ \n" +
                "| |   |_| | | |/ _  )/___)  _)|  ___) |  _ \\ / _  | |  _ \\ / _  )\n" +
                " \\ \\____| |_| ( (/ /|___ | |__| |_____| | | ( ( | | | | | ( (/ / \n" +
                "  \\_____)\\____|\\____|___/ \\___)_______)_| |_|\\_|| |_|_| |_|\\____)\n" +
                "                                            (_____|              \n")
    }

    fun loadSend(version: Int) {
        val pluginCon = QuestEngine.plugin.description

        val state = if (version > 3) "&a√" else "&c×"

        console().sendLang(
            "LOADER-INFO",
            pluginCon.name,
            pluginCon.version,
            "&8[ $state &8]".colored(),
            "1.12-1.19"
        )
    }

}