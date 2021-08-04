package cn.inrhor.questengine.api.dialog

import cn.inrhor.questengine.common.dialog.DialogManager
import cn.inrhor.questengine.utlis.UtilString
import taboolib.common.platform.console
import taboolib.module.lang.sendLang

object DialogAPI {
    /**
     * 注册对话对象
     */
    fun register(dialogID: String, dialogModule: DialogModule) {
        DialogManager.register(dialogID, dialogModule)
    }

    /**
     * 获取对话对象
     */
    fun getDialog(dialogID: String): DialogModule? {
        if (DialogManager.exist(dialogID)) {
            return DialogManager.get(dialogID)
        }
        console().sendLang("DIALOG-NO_EXIST_DIALOG_ID", UtilString.pluginTag, dialogID)
        return null
    }

    /**
     * 删除注册的对话对象
     */
    fun removeDialog(dialogID: String) {
        DialogManager.remove(dialogID)
    }
}