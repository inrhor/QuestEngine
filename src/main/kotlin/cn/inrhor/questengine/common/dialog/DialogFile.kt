package cn.inrhor.questengine.common.dialog

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.common.dialog.DialogManager.register
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
        val dialogFolder = FileUtil.getFile("space/dialog/", "DIALOG-NO_FILES", true,
            "chat", "hologram")

        FileUtil.getFileList(dialogFolder).forEach{
            checkRegDialog(it)
        }

        regWaitDialog(init = true)
    }

    /**
     * 检查配置和注册对话
     */
    private fun checkRegDialog(file: File) {
        val yaml = Configuration.loadFromFile(file, Type.YAML)
        if (yaml.getKeys(false).isEmpty()) {
            console().sendLang("DIALOG-EMPTY_CONTENT", UtilString.pluginTag, file.name)
            return
        }
        for (dialogID in yaml.getKeys(false)) {
            // TODO: 2023/2/26 重写对话注册
            // 对话对象
            val dialog = yaml.getObject<DialogModule>(dialogID, false)
            dialog.dialogID = dialogID
            if (dialog.hook.isEmpty()) {
                dialog.register()
            }else {
                // 列入等待注册行列
                waitMap[dialog] = dialog.hook
            }
        }
    }

    /**
     * 注册处于等待的对话
     */
    private fun regWaitDialog(number: Int = 0, init: Boolean = false) {
        if (!init) {
            console().sendLang("DIALOG_HOOK_REGISTER", UtilString.pluginTag, number, waitMap.values.map { it }.toString())
        }
        val its = waitMap.iterator()
        while (its.hasNext()) {
            val (t, u) = its.next()
            if (DialogManager.exist(u)) {
                val hook = DialogManager.get(u)!!
                t.type = hook.type
                if (t.dialog.isEmpty()) {
                    t.dialog = hook.dialog
                }
                if (t.template.isEmpty()) {
                    t.template = hook.template
                }
                if (t.npcIDs.isEmpty()) {
                    t.npcIDs = hook.npcIDs
                }
                if (t.condition.isEmpty()) {
                    t.condition = hook.condition
                }
                if (t.reply.isEmpty()) {
                    t.reply = hook.reply
                }
                if (!t.space.enable) {
                    t.space.enable = hook.space.enable
                }
                if (t.space.condition.isEmpty()) {
                    t.space.condition = t.condition
                }
                if (t.speed == 1) {
                    t.speed = hook.speed
                }
                if (t.flag.isEmpty()) {
                    t.flag = hook.flag
                }
                if (t.replyChoose.isEmpty()) {
                    t.replyChoose = hook.replyChoose
                }
                if (t.replyDefault.isEmpty()) {
                    t.replyDefault = hook.replyDefault
                }
                t.register()
                its.remove()
            }
        }
        if (waitMap.isNotEmpty()) regWaitDialog(number+1)
    }

    private val waitMap = mutableMapOf<DialogModule, String>()
}