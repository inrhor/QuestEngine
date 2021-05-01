package cn.inrhor.questengine.common.hologram.asi

import cn.inrhor.questengine.utlis.public.MsgUtil
import java.util.HashMap
import java.util.LinkedHashMap

class HoloManagerID {
    companion object {
        private var holoIntIDMap: HashMap<String, Int> = LinkedHashMap()
    }

    fun generate(holoID: String, type: String, index: Int, replyID: String): Int {
        val strID = "$holoID-$type-$index$replyID"
        if (holoIntIDMap[strID] != null) MsgUtil.send("ID 已存在")
        holoIntIDMap[strID] = strID.hashCode()
        return strID.hashCode()
    }

}