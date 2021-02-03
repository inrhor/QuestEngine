package cn.inrhor.questengine.common.dialog

import cn.inrhor.questengine.QuestEngine
import io.izzel.taboolib.util.Files
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
        dialogFileMap[dialogID] = dialogFile
    }

    /**
     * 加载并注册对话
     */
    fun loadDialog() {
        val dialogFolder = getFile()
        getYamlList(dialogFolder).forEach{
            checkDialog(it)
        }
    }

    /**
     * 检查和注册对话
     */
    private fun checkDialog(yaml: YamlConfiguration) {
        for (id in yaml.getKeys(false)) {
            val config = yaml.getConfigurationSection(id)!!
            if (config.getKeys(false).isEmpty()) { // 开头不标准

            } else {
                DialogFile().init(config)
            }
        }
    }

    /**
     * 返回 dialog 文件夹的内容
     */
    private fun getFile(): File {
        val file = File(QuestEngine.plugin.dataFolder, "dialog")
        if (!file.exists()) { // 如果 dialog 文件夹不存在就给示例配置
            Files.releaseResource(QuestEngine.plugin, "dialog/example.yml", true)
        }
        return file
    }

    /**
     * 返回所有 dialog 配置
     */
    private fun getYamlList(file: File): List<YamlConfiguration> =
        mutableListOf<YamlConfiguration>().let { yamlList ->
            if (file.isDirectory) {
                file.listFiles()!!.forEach { yamlList.addAll(getYamlList(it)) }
            }else if (file.name.endsWith(".yml", true)) {
                yamlList.add(YamlConfiguration.loadConfiguration(file))
            }
            return@let yamlList
        }

    /**
     * 删除对话
     */
    fun remove(dialogID: String) {dialogFileMap.remove(dialogID)}
}