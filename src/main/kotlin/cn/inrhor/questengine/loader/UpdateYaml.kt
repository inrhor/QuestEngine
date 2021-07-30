package cn.inrhor.questengine.loader

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.utlis.public.UtilString
import taboolib.common.platform.console
import taboolib.module.configuration.migrateTo
import taboolib.module.lang.sendLang
import java.io.File
import java.io.FileInputStream

object UpdateYaml {

    private fun from(child : String): ByteArray? {
        val plugin = QuestEngine.plugin
        val yaml = File(plugin.dataFolder, child)
        val file = FileInputStream(yaml)
        val resource = plugin.getResource(child)?: return null
        return file.migrateTo(resource)
    }

    fun run(child: String) {
        val from = from(child)?:
        return console().sendLang("LOADER.FILE_FAIL_UPDATE", UtilString.pluginTag, child)
        val file = File(QuestEngine.plugin.dataFolder, child)
        file.writeBytes(from)
    }

}