package cn.inrhor.questengine.api.dialog

import cn.inrhor.questengine.common.dialog.DialogFile
import cn.inrhor.questengine.common.dialog.DialogManager
import cn.inrhor.questengine.utlis.public.UseString
import io.izzel.taboolib.module.locale.TLocale

class Dialog {
    /**
     * 获取对话配置
     */
    fun getDialog(dialogID: String): DialogFile? {
        if (DialogManager().exist(dialogID)) {
            return DialogManager().get(dialogID)
        }
        TLocale.sendToConsole("DIALOG.NO_EXIST_DIALOG_ID", UseString.pluginTag, dialogID)
        return null
    }

    /**
     * 删除注册的对话配置
     */
    fun removeDialog(dialogID: String) {
        DialogManager().remove(dialogID)
    }
}