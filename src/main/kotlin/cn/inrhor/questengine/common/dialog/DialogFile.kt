package cn.inrhor.questengine.common.dialog

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.api.dialog.SpaceDialogModule
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
        val dialogFolder = FileUtil.getFile("space/dialog/", "DIALOG-NO_FILES", true,
            "chat", "hologram")

        FileUtil.getFileList(dialogFolder).forEach{
            checkRegDialog(it, dialogFolder)
        }

        waitMap.forEach { (t, u) ->
            if (DialogManager.exist(u) && DialogManager.exist(t)) {
                DialogManager.get(t)!!.reply = DialogManager.get(u)!!.reply
            }
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
                        regDialog(dialogID, yaml, ifs, id)
                        return@forEach
                    }
                }
            }else {
                regDialog(dialogID, yaml, cfs)
            }
        }
    }

    /**
     * 以节点为 DialogID 进行注册对话模块
     */
    private fun regDialog(dialogID: String, file: Configuration, hookSection: ConfigurationSection, hookID: String = dialogID) {
        val dialogModule = file.getObject<DialogModule>(dialogID, false)
        dialogModule.dialogID = dialogID
        if (dialogID != hookID) {
            if (!file.contains("$dialogID.dialog")) dialogModule.dialog = hookSection.getStringList("dialog")
            if (!file.contains("$dialogID.npcIDs")) dialogModule.npcIDs = hookSection.getStringList("npcIDs")
            if (!file.contains("$dialogID.condition")) dialogModule.condition = hookSection.getString("condition")?: ""
            if (!file.contains("$dialogID.space")) dialogModule.space = if (hookSection.contains("space")) hookSection.getObject("space", false) else SpaceDialogModule()
            if (!file.contains("$dialogID.reply")) {
                if (hookSection.contains("reply")) waitMap[dialogID] = hookID else dialogModule.reply = mutableListOf()
            }
            if (!file.contains("$dialogID.type")) {
                if (hookSection.contains("type")) dialogModule.type = hookSection.getString("type")?: "chat"
            }
            if (!file.contains("$dialogID.template")) {
                if (hookSection.contains("template")) dialogModule.template = hookSection.getString("template")?: ""
            }
            if (!file.contains("$dialogID.replyChoose")) {
                if (hookSection.contains("replyChoose")) dialogModule.template = hookSection.getString("replyChoose")?: ""
            }
            if (!file.contains("$dialogID.replyDefault")) {
                if (hookSection.contains("replyDefault")) dialogModule.template = hookSection.getString("replyDefault")?: ""
            }
        }
        dialogModule.register()
    }

    private val waitMap = mutableMapOf<String, String>()
}