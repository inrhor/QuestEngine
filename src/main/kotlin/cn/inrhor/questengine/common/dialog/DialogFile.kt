package cn.inrhor.questengine.common.dialog

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.api.dialog.ReplyModule
import cn.inrhor.questengine.api.dialog.SpaceModule
import cn.inrhor.questengine.utlis.UtilString
import taboolib.library.configuration.YamlConfiguration
import taboolib.common.platform.function.*
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.lang.sendLang
import java.io.File

object DialogFile {

    /**
     * 检查配置和注册对话
     */
    fun checkRegDialog(file: File) {
        val yaml = YamlConfiguration.loadConfiguration(file)
        if (yaml.getKeys(false).isEmpty()) {
            console().sendLang("DIALOG-EMPTY_CONTENT", UtilString.pluginTag, file.name)
            return
        }
        for (dialogID in yaml.getKeys(false)) {
            val cfs = yaml.getConfigurationSection(dialogID)

            if (cfs.contains("hook")) {
                val id = cfs.getString("hook")!!
                regDialog(yaml.getConfigurationSection(id), cfs)
            }else {
                regDialog(cfs)
            }

        }
    }

    private fun regDialog(section: ConfigurationSection, hook: ConfigurationSection = section) {
        val dialogID = section.name

        val npcID = hook.getStringList("npcIDs")
        val condition = hook.getStringList("condition")
        val type = hook.getString("type")?: "holo"

        val dialog = section.getStringList("dialog")

        val enableSpace = hook.getBoolean("space.enable")
        val listSpace = hook.getStringList("space.condition")
        val space = SpaceModule(enableSpace, listSpace)

        val dialogModule = DialogModule(
            dialogID, npcID, condition, type, dialog,
            mutableListOf(), mutableListOf(),
            space)

        addReply(section, dialogID, dialogModule)
        if (dialogID != hook.name) {
            addReply(hook, dialogID, dialogModule)
        }

        DialogManager.register(dialogID, dialogModule)
    }

    private fun addReply(section: ConfigurationSection, dialogID: String, dialogModule: DialogModule) {
        if (section.contains("reply")) {
            val replySfc = section.getConfigurationSection("reply")
            if (replySfc.getKeys(false).isNotEmpty()) {
                for (replyID in replySfc.getKeys(false)) {
                    val content = replySfc.getStringList("$replyID.content")
                    val script = replySfc.getStringList("$replyID.script")
                    val cd = replySfc.getStringList("$replyID.condition")
                    val replyCube = ReplyModule(dialogID, replyID, content, script, cd)
                    dialogModule.replyModuleList.add(replyCube)
                }
            }
        }
    }

}