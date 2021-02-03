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
        dialogFileMap[dialogID] = dialogFile
    }

    /**
     * 加载并注册对话
     */
    fun loadDialog() {
        Bukkit.getScheduler().runTaskAsynchronously(QuestEngine.plugin, Runnable {
            val dialogFolder = getFile()
            getYamlList(dialogFolder).forEach{
                MsgUtil.send("it  "+it.name)
                checkDialog(it)
            }
        })
    }

    /**
     * 检查和注册对话
     */
    private fun checkDialog(yaml: YamlConfiguration) {
        // 我忘了我写了什么玩意

        MsgUtil.send("aaa $yaml")
        if (yaml.getKeys(false).isEmpty()) {
            MsgUtil.send("bbb empty")
            TLocale.sendToConsole("DIALOG.EMPTY_CONTENT", UseString.pluginTag, yaml.name)
            return
        }
        /*MsgUtil.send("ccc ${yaml.name}")
        if (exist(yaml.name)) {
            // say
            MsgUtil.send("ddd ${yaml.name}")
            return
        }
        MsgUtil.send("eee ${yaml.name}")
        DialogFile().init(yaml)*/

        /*for (id in yaml.getKeys(false)) {
            val config = yaml.getConfigurationSection(id)!!
            if (config.getKeys(false).isEmpty()) {
                TLocale.sendToConsole("DIALOG.EMPTY_CONTENT", UseString.pluginTag, yaml.name)
            } else {
                if (exist(config.name)) {
                    // say
                    return
                }
                DialogFile().init(config)
            }
        }*/
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

    fun exist(dialogID: String) = dialogFileMap.contains(dialogID)

    fun get(dialogID: String) = dialogFileMap[dialogID]
}