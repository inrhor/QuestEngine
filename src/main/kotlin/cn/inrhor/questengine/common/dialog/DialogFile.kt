package cn.inrhor.questengine.common.dialog

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.utlis.UtilString
import cn.inrhor.questengine.utlis.file.FileUtil
import taboolib.common.platform.function.console
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Configuration.Companion.getObject
import taboolib.module.configuration.Type
import taboolib.module.lang.sendLang
import java.io.File

object DialogFile {

    fun loadDialog() {
        val dialogFolder = FileUtil.getFile("space/dialog/", "DIALOG-NO_FILES", true)

        FileUtil.getFileList(dialogFolder).forEach{
            checkRegDialog(it, dialogFolder)
        }
    }

    /**
     * 检查配置和注册对话
     */
    private fun checkRegDialog(file: File, folder: File) {
        val yaml = Configuration.loadFromFile(file, Type.YAML)
        if (yaml.getKeys(false).isEmpty()) {
            console().sendLang("DIALOG-EMPTY_CONTENT", UtilString.pluginTag, file.name)
            return
        }
        for (dialogID in yaml.getKeys(false)) {
            val cfs = yaml.getConfigurationSection(dialogID)?: return

            if (cfs.contains("hook")) {
                val id = cfs.getString("hook")!!
                FileUtil.getFileList(folder).forEach{
                    val hook = Configuration.loadFromFile(it)
                    val ifs = hook.getConfigurationSection(id)
                    if (ifs != null) {
                        regDialog(dialogID, hook)
                    }
                }
            }else {
                regDialog(dialogID, yaml)
            }
        }
    }

    /**
     * 以节点为 DialogID 进行注册对话模块
     */
    private fun regDialog(dialogID: String, file: Configuration) {
        val dialogModule = file.getObject<DialogModule>(dialogID, ignoreConstructor = true)
        dialogModule.dialogID = dialogID
        dialogModule.register()
    }

}