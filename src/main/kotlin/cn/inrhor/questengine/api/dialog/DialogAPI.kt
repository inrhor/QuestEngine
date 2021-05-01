package cn.inrhor.questengine.api.dialog

import cn.inrhor.questengine.common.dialog.DialogManager
import cn.inrhor.questengine.utlis.public.UseString
import io.izzel.taboolib.module.locale.TLocale

class DialogAPI {
    /**
     * 注册对话对象
     */
    fun register(dialogID: String, dialogModule: DialogModule) {
        DialogManager().register(dialogID, dialogModule)
    }

    /**
     * 获取对话对象
     */
    fun getDialog(dialogID: String): DialogModule? {
        if (DialogManager().exist(dialogID)) {
            return DialogManager().get(dialogID)
        }
        TLocale.sendToConsole("DIALOG.NO_EXIST_DIALOG_ID", UseString.pluginTag, dialogID)
        return null
    }

    /**
     * 删除注册的对话对象
     */
    fun removeDialog(dialogID: String) {
        DialogManager().remove(dialogID)
    }
}