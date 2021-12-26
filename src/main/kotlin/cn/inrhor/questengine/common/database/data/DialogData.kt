package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.api.dialog.theme.DialogTheme
import cn.inrhor.questengine.api.dialog.theme.ReplyTheme
import cn.inrhor.questengine.common.dialog.theme.hologram.core.HoloHitBox

data class DialogData(
    var dialogMap: MutableMap<String, MutableList<DialogTheme>>,
    var replyMap: MutableMap<String, MutableList<ReplyTheme>>,
    var holoBoxMap: MutableMap<String, MutableList<HoloHitBox>>) {

    fun addDialog(dialogID: String, dialogTheme: DialogTheme) {
        val map = dialogMap[dialogID]?: mutableListOf()
        map.add(dialogTheme)
        dialogMap[dialogID] = map
    }

    fun addReply(dialogID: String, replyTheme: ReplyTheme) {
        val map = replyMap[dialogID]?: mutableListOf()
        map.add(replyTheme)
        replyMap[dialogID] = map
    }

    fun addHoloBox(dialogID: String, holoHitBox: HoloHitBox) {
        val boxMap = holoBoxMap[dialogID]?: mutableListOf()
        if (!boxMap.equals(holoHitBox)) {
            boxMap.add(holoHitBox)
        }
        holoBoxMap[dialogID] = boxMap
    }

    /**
     * 交互全息触发终止全息对话
     */
    fun endHoloDialog(dialogID: String) {
        val holoDialog = dialogMap[dialogID]?: return
        holoDialog.forEach {
            it.end()
        }
        val holoReply = replyMap[dialogID]?: return
        holoReply.forEach {
            it.end()
        }
        val holoBox = holoBoxMap[dialogID]?: return
        holoBox.forEach {
            it.dialogHolo.end()
        }
        dialogMap.remove(dialogID)
        replyMap.remove(dialogID)
        holoBoxMap.remove(dialogID)
    }

}