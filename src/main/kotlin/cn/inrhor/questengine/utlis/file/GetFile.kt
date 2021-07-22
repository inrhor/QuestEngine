package cn.inrhor.questengine.utlis.file

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.utlis.public.UseString
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.Files
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object GetFile {
    /**
     * 返回文件夹的内容
     */
    fun getFile(child: String, say: String, mkdir: Boolean): File {
        val file = File(QuestEngine.plugin.dataFolder, child)
        if (!file.exists() && mkdir) { // 如果 <child> 文件夹不存在就给示例配置
            TLocale.sendToConsole(say, UseString.pluginTag)
            Files.releaseResource(QuestEngine.plugin, "$child/example.yml", true)
        }
        return file
    }

    fun getFileList(file: File): List<File> =
        mutableListOf<File>().let { files ->
            if (file.isDirectory) {
                file.listFiles()!!.forEach { files.addAll(getFileList(it)) }
            }else if (file.name.endsWith(".yml", true)) {
                files.add(file)
            }
            return@let files
        }

    fun yaml(path: String, yaml: String): YamlConfiguration {
        val str = "$path/$yaml.yml"
        val file = File(QuestEngine.plugin.dataFolder, str)
        if (!file.exists()) {
            Files.releaseResource(QuestEngine.plugin, str, true)
        }
        return YamlConfiguration.loadConfiguration(file)
    }
}