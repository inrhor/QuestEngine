package cn.inrhor.questengine.common.dialog

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.api.dialog.ReplyModule
import cn.inrhor.questengine.utlis.public.UtilString
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object DialogFile {

    /**
     * 检查配置和注册对话
     */
    fun checkRegDialog(file: File) {
        val yaml = YamlConfiguration.loadConfiguration(file)
        if (yaml.getKeys(false).isEmpty()) {
            console().sendLang("DIALOG.EMPTY_CONTENT", UtilString.pluginTag, file.name)
            return
        }
        for (dialogID in yaml.getKeys(false)) {
            val cfs = yaml.getConfigurationSection(dialogID)!!
            if (!cfs.contains("npcIDs")) {
                return run {
                    console().sendLang("DIALOG.ERROR_FILE", dialogID)
                }
            }
            if (!cfs.contains("condition")) {
                return run {
                    console().sendLang("DIALOG.ERROR_FILE", dialogID)
                }
            }

            val npcID = cfs.getStringList("npcIDs")
            val condition = cfs.getStringList("condition")
            val type = cfs.getString("type")?: "holo"
            val dialog = cfs.getStringList("dialog")

            val dialogModule = DialogModule(
                dialogID, npcID, condition, type, dialog, mutableListOf(), mutableListOf())

            if (cfs.contains("reply")) {
                val replySfc = cfs.getConfigurationSection("reply")!!
                if (replySfc.getKeys(false).isNotEmpty()) {
                    for (replyID in replySfc.getKeys(false)) {
                        val content = replySfc.getStringList("$replyID.content")
                        val script = replySfc.getStringList("$replyID.script")
                        val replyCube = ReplyModule(dialogID, replyID, content, script)
                        replyCube.holoInit()
                        dialogModule.replyModuleList.add(replyCube)
                    }
                }
            }

            DialogManager.register(dialogID, dialogModule)
        }
    }

}