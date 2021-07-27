package cn.inrhor.questengine.loader

import cn.inrhor.questengine.QuestEngine
import io.izzel.taboolib.module.config.TConfigMigrate
import io.izzel.taboolib.util.Files
import java.io.File
import java.io.FileInputStream

object UpdateYaml {

    private fun from(child : String): MutableList<String> {
        val plugin = QuestEngine.plugin
        val yaml = File(plugin.dataFolder, child)
        val file = FileInputStream(yaml)
        return TConfigMigrate.migrate(file, plugin.getResource(child))
    }

    fun run(child: String) {
        val file = File(QuestEngine.plugin.dataFolder, child)
        Files.write(file) { w ->
            for (line in from(child)) {
                w.write(line)
                w.newLine()
            }
        }
    }

}