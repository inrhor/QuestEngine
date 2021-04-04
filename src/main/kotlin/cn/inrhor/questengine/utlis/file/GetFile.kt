package cn.inrhor.questengine.utlis.file

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.utlis.public.UseString
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.Files
import java.io.File

class GetFile {
    /**
     * 返回文件夹的内容
     */
    fun getFile(child: String, say: String): File {
        val file = File(QuestEngine.plugin.dataFolder, child)
        if (!file.exists()) { // 如果 <child> 文件夹不存在就给示例配置
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
}