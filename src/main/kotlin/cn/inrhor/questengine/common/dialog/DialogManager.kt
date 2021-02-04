package cn.inrhor.questengine.common.dialog

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.utlis.public.MsgUtil
import cn.inrhor.questengine.utlis.public.UseString
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.Files
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*


class DialogManager {
    companion object {
        /**
         * 成功注册的对话
         */
        private var dialogFileMap: HashMap<String, DialogFile> = LinkedHashMap<String, DialogFile>()
    }

    /**
     * 注册对话
     */
    fun register(dialogID: String, dialogFile: DialogFile) {
        if (exist(dialogID)) {
            TLocale.sendToConsole("DIALOG.EXIST_DIALOG_ID", UseString.pluginTag, dialogID)
            return
        }
        dialogFileMap[dialogID] = dialogFile
    }

    /**
     * 加载并注册对话
     */
    fun loadDialog() {
        Bukkit.getScheduler().runTaskAsynchronously(QuestEngine.plugin, Runnable {
            val dialogFolder = getFile()
            getFileList(dialogFolder).forEach{
                MsgUtil.send("it  "+it.name)
                checkDialog(it)
            }
        })
    }

    /**
     * 检查和注册对话
     */
    private fun checkDialog(file: File) {
        val yaml = YamlConfiguration.loadConfiguration(file)
        if (yaml.getKeys(false).isEmpty()) {
            TLocale.sendToConsole("DIALOG.EMPTY_CONTENT", UseString.pluginTag, file.name)
            return
        }
        for (dialogID in yaml.getKeys(false)) {
            DialogFile().init(yaml.getConfigurationSection(dialogID)!!)
        }
    }

    /**
     * 返回 dialog 文件夹的内容
     */
    private fun getFile(): File {
        val file = File(QuestEngine.plugin.dataFolder, "dialog")
        if (!file.exists()) { // 如果 dialog 文件夹不存在就给示例配置
            TLocale.sendToConsole("DIALOG.NO_FILES", UseString.pluginTag)
            Files.releaseResource(QuestEngine.plugin, "dialog/example.yml", true)
        }
        return file
    }

    /**
     * 返回所有 dialog 配置文件
     */
    private fun getFileList(file: File): List<File> =
        mutableListOf<File>().let { files ->
            if (file.isDirectory) {
                file.listFiles()!!.forEach { files.addAll(getFileList(it)) }
            }else if (file.name.endsWith(".yml", true)) {
                files.add(file)
            }
            return@let files
        }

    /**
     * 删除对话
     */
    fun remove(dialogID: String) {dialogFileMap.remove(dialogID)}

    fun exist(dialogID: String) = dialogFileMap.contains(dialogID)

    fun get(dialogID: String) = dialogFileMap[dialogID]
}