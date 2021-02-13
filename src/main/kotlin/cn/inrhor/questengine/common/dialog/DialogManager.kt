package cn.inrhor.questengine.common.dialog

import cn.inrhor.questengine.utlis.file.GetFile
import cn.inrhor.questengine.utlis.public.UseString
import io.izzel.taboolib.module.locale.TLocale
import java.util.*


class DialogManager {
    companion object {
        /**
         * 成功注册的对话
         */
        private var dialogFileMap: HashMap<String, DialogCube> = LinkedHashMap()
    }

    /**
     * 注册对话
     */
    fun register(dialogID: String, dialogCube: DialogCube) {
        if (exist(dialogID)) {
            TLocale.sendToConsole("DIALOG.EXIST_DIALOG_ID", UseString.pluginTag, dialogID)
            return
        }
        dialogFileMap[dialogID] = dialogCube
    }

    /**
     * 加载并注册对话
     */
    fun loadDialog() {
        val dialogFolder = GetFile().getFile("dialog", "DIALOG.NO_FILES")
        GetFile().getFileList(dialogFolder).forEach{
            DialogFile().checkRegDialog(it)
        }
    }

    /**
     * 删除对话
     */
    fun remove(dialogID: String) {dialogFileMap.remove(dialogID)}

    /**
     * 对话ID 是否存在
     */
    fun exist(dialogID: String) = dialogFileMap.contains(dialogID)

    /**
     * 获取对话对象
     */
    fun get(dialogID: String) = dialogFileMap[dialogID]

    /**
     * 清空对话对象Map
     */
    fun clearMap() = dialogFileMap.clear()
}