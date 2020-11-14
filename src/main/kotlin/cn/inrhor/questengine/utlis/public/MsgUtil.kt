package cn.inrhor.questengine.utlis.public

import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.Bukkit

object MsgUtil {

    fun send(msg: String) {
        Bukkit.getConsoleSender().sendMessage(TLocale.Translate.setColored(msg))
    }

}