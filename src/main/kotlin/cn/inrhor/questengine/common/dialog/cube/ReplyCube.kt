package cn.inrhor.questengine.common.dialog.cube

import cn.inrhor.questengine.common.dialog.location.FixedLocation

class ReplyCube(val replyID:String,
                var textAddLoc: FixedLocation, var textContent: MutableList<String>,
                var itemAddLoc: FixedLocation, var itemContent: MutableList<String>) {
}