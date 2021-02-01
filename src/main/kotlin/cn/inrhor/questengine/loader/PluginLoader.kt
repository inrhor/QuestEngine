package cn.inrhor.questengine.loader

import cn.inrhor.questengine.utlis.public.UseString
import com.comphenix.protocol.ProtocolLibrary

class PluginLoader {

    fun init() {
        UpdateYaml().run(UseString.getLang())
        InfoSend().logoSend()

//        ClickHoloListener().click()
    }

    fun active() {
//        ProtocolLibrary.getProtocolManager().addPacketListener(ClickHoloListener())
    }

    /*fun cancel() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(QuestEngine.plugin)
    }*/

}