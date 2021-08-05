package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.common.dialog.optional.holo.HoloHitBox
import cn.inrhor.questengine.common.dialog.optional.holo.core.HoloDialog
import cn.inrhor.questengine.common.dialog.optional.holo.core.HoloReply

class DialogData(var holoDialogMap: MutableMap<String, MutableList<HoloDialog>>,
                 var holoReplyMap: MutableMap<String, MutableList<HoloReply>>,
                 var holoBoxMap: MutableMap<String, MutableList<HoloHitBox>>) {

    /**
     * 交互全息触发终止全息对话
     */
    fun endHoloDialog(holoID: String, holoBox: HoloHitBox) {
        val iterator = holoDialogMap.iterator()
        while (iterator.hasNext()){
            val holoDialog = iterator.next()
            val dialogModule = holoDialog.dialogModule
            if (dialogModule.dialogID == holoBox.replyModule.dialogID) {
                holoDialog.end()
                iterator.remove()
                break
            }
        }
        for (holoReply in holoReplyMap) {
            holoReply.end()
        }
        holoReplyMap.clear()
        for (holoHitBox in holoBoxMap) {
            holoHitBox.end()
        }
        holoBoxMap.clear()
    }

}