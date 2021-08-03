package cn.inrhor.questengine.loader

import cn.inrhor.questengine.QuestEngine
import taboolib.module.configuration.migrateTo
import java.io.File

object UpdateYaml {

    fun run(child: String) {
        val plugin = QuestEngine.plugin
        val file = File(plugin.dataFolder, child)
        val resource = plugin.getResource(child)?: return
        val bytes = resource.migrateTo(file.inputStream())?: return
        file.writeBytes(bytes)
    }

}