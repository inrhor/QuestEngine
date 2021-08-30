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
                regDialog(cfs, yaml.getConfigurationSection(id))
            }else {
                regDialog(cfs)
            }

        }
    }

    private fun regDialog(section: ConfigurationSection, hook: ConfigurationSection = section) {
        val dialogID = section.name

        val npcID = if (section.contains("npcIDs")) section.getStringList("npcIDs") else hook.getStringList("npcIDs")

        val condition = if (section.contains("condition")) section.getStringList("condition") else hook.getStringList("condition")

        val type = if (section.contains("type")) section.getString("type") else hook.getString("type")?: "holo"

        val dialog = if (section.contains("dialog")) section.getStringList("dialog") else hook.getStringList("dialog")

        val e = "space.enable"
        val enableSpace = if (section.contains(e)) section.getBoolean(e) else hook.getBoolean(e)
        val es = "space.condition"
        val listSpace = if (section.contains(es)) section.getStringList(es) else hook.getStringList(es)
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