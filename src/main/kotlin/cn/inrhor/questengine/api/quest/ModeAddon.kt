package cn.inrhor.questengine.api.quest

import cn.inrhor.questengine.common.quest.ModeType

data class ModeAddon(
    var type: ModeType = ModeType.PERSONAL,
    var amount: Int = -1,
    var shareData: Boolean = false)