package cn.inrhor.questengine.common.dialog

import cn.inrhor.questengine.utlis.file.GetFile
import cn.inrhor.questengine.utlis.public.UseString
import io.izzel.taboolib.module.locale.TLocale
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
        val dialogFolder = GetFile().getFile("dialog", "DIALOG.NO_FILES")
        GetFile().getFileList(dialogFolder).forEach{
            checkRegDialog(it)
        }
    }

    /**
     * 检查和注册对话
     */
    private fun checkRegDialog(file: File) {
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
     * 删除对话
     */
    fun remove(dialogID: String) {dialogFileMap.remove(dialogID)}

    /**
     * 对话ID 是否存在
     */
    fun exist(dialogID: String) = dialogFileMap.contains(dialogID)

    /**
     * 获取对话配置内容
     */
    fun get(dialogID: String) = dialogFileMap[dialogID]
}