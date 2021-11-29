package cn.inrhor.questengine.common.dialog

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.utlis.UtilString
import cn.inrhor.questengine.utlis.file.FileUtil
import taboolib.common.platform.function.console
import taboolib.library.configuration.ConfigurationSection
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
                    val hook = Configuration.loadFromFile(it, Type.YAML)
                    val ifs = hook.getConfigurationSection(id)
                    if (ifs != null) {
                        regDialog(cfs, ifs)
                    }
                }
            }else {
                regDialog(cfs)
            }
        }
    }

    private fun regDialog(section: ConfigurationSection, hook: ConfigurationSection = section) {
        val id = section.name
        val dialogModule = hook.getObject<DialogModule>(id, ignoreConstructor = true)
        dialogModule.dialogID = id
        dialogModule.register()
    }

}