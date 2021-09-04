package cn.inrhor.questengine.utlis.file

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.server.UpdateYaml
import cn.inrhor.questengine.utlis.UtilString
import taboolib.library.configuration.YamlConfiguration
import taboolib.common.platform.function.*
import taboolib.module.lang.sendLang
import java.io.File

object FileUtil {
    /**
     * 返回文件夹的内容
     */
    fun getFile(child: String, say: String, mkdirs: Boolean): File {
        val file = File(QuestEngine.plugin.dataFolder, child)
        if (!file.exists() && mkdirs) { // 如果 <child> 文件夹不存在就给示例配置
            if (say.isNotEmpty()) {
                console().sendLang(say, UtilString.pluginTag)
            }
            QuestEngine.resource.releaseResourceFile(child+"example.yml", true)
        }
        return file
    }

    fun getFile(child: String): File {
        return getFile(child, "", false)
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
        return releaseFile(str)
    }
}

fun releaseFile(child: String): YamlConfiguration {
    val file = File(QuestEngine.plugin.dataFolder, child)
    if (!file.exists()) {
        QuestEngine.resource.releaseResourceFile(child, true)
    }
    UpdateYaml.run(child)
    return YamlConfiguration.loadConfiguration(file)
}