package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.common.dialog.optional.holo.HoloHitBox
import cn.inrhor.questengine.common.dialog.optional.holo.core.HoloDialog
import cn.inrhor.questengine.common.dialog.optional.holo.core.HoloReply

class DialogData(var holoDialogList: MutableList<HoloDialog>,
                 var holoReplyList: MutableList<HoloReply>,
                 var holoBoxList: MutableList<HoloHitBox>,) {

    /**
     * 交互全息触发终止全息对话
     */
    fun endHoloDialog(holoBox: HoloHitBox) {
        val iterator = holoDialogList.iterator()
        while (iterator.hasNext()){
            val holoDialog = iterator.next()
            val dialogModule = holoDialog.dialogModule
            if (dialogModule.dialogID == holoBox.replyModule.dialogID) {
                holoDialog.end()
                iterator.remove()
                break
            }
        }
        for (holoReply in holoReplyList) {
            holoReply.end()
        }
        holoReplyList.clear()
        for (holoHitBox in holoBoxList) {
            holoHitBox.end()
        }
        holoBoxList.clear()
    }

}