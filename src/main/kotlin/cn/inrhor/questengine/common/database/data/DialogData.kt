package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.common.dialog.optional.holo.HoloHitBox
import cn.inrhor.questengine.common.dialog.optional.holo.core.HoloDialog
import cn.inrhor.questengine.common.dialog.optional.holo.core.HoloReply

class DialogData(var holoDialogMap: MutableMap<String, MutableList<HoloDialog>>,
                 var holoReplyMap: MutableMap<String, MutableList<HoloReply>>,
                 var holoBoxMap: MutableMap<String, MutableList<HoloHitBox>>) {

    fun addHoloDialog(dialogID: String, holoDialog: HoloDialog) {
        val dialogMap = holoDialogMap[dialogID]?: mutableListOf()
        dialogMap.add(holoDialog)
        holoDialogMap[dialogID] = dialogMap
    }

    fun addHoloReply(dialogID: String, holoReply: HoloReply) {
        val replyMap = holoReplyMap[dialogID]?: mutableListOf()
        replyMap.add(holoReply)
        holoReplyMap[dialogID] = replyMap
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
        val holoDialog = holoDialogMap[dialogID]?: return
        holoDialog.forEach {
            it.end()
        }
        val holoReply = holoReplyMap[dialogID]?: return
        holoReply.forEach {
            it.end()
        }
        val holoBox = holoBoxMap[dialogID]?: return
        holoBox.forEach {
            it.end()
        }
        holoDialogMap.remove(dialogID)
        holoReplyMap.remove(dialogID)
        holoBoxMap.remove(dialogID)
    }

}