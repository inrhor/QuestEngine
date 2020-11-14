package cn.inrhor.questengine.loader

import cn.inrhor.questengine.IElodieQuest
import com.tchristofferson.configupdater.ConfigUpdater
import java.io.File

class UpdateYaml {
    fun run(lang : String) {
        val plugin = IElodieQuest.plugin
        ConfigUpdater.update(plugin, "config.yml", IElodieQuest.config.file, emptyList())

        val langName = "lang/${lang}.yml"
        val langFile = File(plugin.dataFolder, langName)
        ConfigUpdater.update(plugin, langName, langFile, emptyList())
    }
}