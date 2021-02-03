package cn.inrhor.questengine.api.dialog

import cn.inrhor.questengine.common.dialog.DialogFile
import cn.inrhor.questengine.common.dialog.DialogManager

class Dialog {
    /**
     * 获取对话对象
     */
    fun getDialog(dialogID: String): DialogFile? {
        if (DialogManager().exist(dialogID)) {
            return DialogManager().get(dialogID)
        }
        // say
        return null
    }

    /**
     * 删除对话
     */
    fun removeDialog(dialogID: String) {
        DialogManager().remove(dialogID)
    }
}