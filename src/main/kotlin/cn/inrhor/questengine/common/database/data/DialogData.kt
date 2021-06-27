package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.common.dialog.optional.holo.HoloHitBox
import cn.inrhor.questengine.common.dialog.optional.holo.core.HoloDialog
import cn.inrhor.questengine.common.dialog.optional.holo.core.HoloReply

class DialogData(var holoDialogList: MutableList<HoloDialog>,
                 var holoReplyList: MutableList<HoloReply>,
                 var holoBoxList: MutableList<HoloHitBox>,) {
}