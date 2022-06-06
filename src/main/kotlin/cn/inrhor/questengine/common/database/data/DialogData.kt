package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.api.dialog.theme.DialogTheme
import cn.inrhor.questengine.api.dialog.theme.ReplyTheme
import cn.inrhor.questengine.common.dialog.theme.hologram.core.HoloHitBox

data class DialogData(
    val dialogMap: MutableMap<String, DialogTheme> = mutableMapOf(),
    val replyMap: MutableMap<String, MutableList<ReplyTheme>> = mutableMapOf(),
    val holoBoxMap: MutableMap<String, MutableList<HoloHitBox>> = mutableMapOf()) {

    fun addDialog(dialogID: String, dialogTheme: DialogTheme) {
        dialogMap[dialogID] = dialogTheme
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
        holoDialog.end()
        val holoReply = replyMap[dialogID]?: return
        holoReply.forEach {
            it.end()
        }
        val holoBox = holoBoxMap[dialogID]?: return
        holoBox.forEach {
            it.hitBoxList.forEach { b ->
                b.endHitBox()
            }
        }
        dialogMap.remove(dialogID)
        replyMap.remove(dialogID)
        holoBoxMap.remove(dialogID)
    }

}