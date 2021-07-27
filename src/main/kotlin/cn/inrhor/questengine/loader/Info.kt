package cn.inrhor.questengine.loader

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.utlis.public.MsgUtil
import cn.inrhor.questengine.utlis.public.UtilString
import io.izzel.taboolib.module.locale.TLocale

object Info {

    fun logoSend() {
        val logo = listOf(
            "   ____                  _   ______             _            ",
            "  / __ \\                | | |  ____|           (_)           ",
            " | |  | |_   _  ___  ___| |_| |__   _ __   __ _ _ _ __   ___ ",
            " | |  | | | | |/ _ \\/ __| __|  __| | '_ \\ / _` | | '_ \\ / _ \\",
            " | |__| | |_| |  __/\\__ \\ |_| |____| | | | (_| | | | | |  __/",
            "  \\___\\_\\\\__,_|\\___||___/\\__|______|_| |_|\\__, |_|_| |_|\\___|",
            "                                           __/ |             ",
            "                                          |___/              "
        )
        for (s in logo) {
            MsgUtil.send(s)
        }

        val pluginCon = QuestEngine.plugin.description

        TLocale.sendToConsole(
            "LOADER.INFO",
            pluginCon.name,
            pluginCon.version,
            UtilString.getLang()
        )
    }

}