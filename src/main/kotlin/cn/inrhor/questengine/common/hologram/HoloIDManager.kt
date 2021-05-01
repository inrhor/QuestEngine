package cn.inrhor.questengine.common.hologram

/**
 * Manager > HoloID & EntityID
 * 为了注册时自动给定EntityID
 */
class HoloIDManager {
    companion object {
        private val holoEntityIDs: MutableSet<Int> = mutableSetOf()
    }

    /**
     * type > text, item
     */
    fun generate(holoID: String, index: Int, type: String): Int {
        return "$holoID-dialog-$index-$type".hashCode()
    }

    fun generate(holoID: String, replyID: String, index: Int, type: String): Int {
        return "$holoID-reply-$replyID-$index-$type".hashCode()
    }

    fun existEntityID(holoEntityID: Int): Boolean {
        return holoEntityIDs.contains(holoEntityID)
    }

    fun addEntityID(holoEntityID: Int) {
        holoEntityIDs.add(holoEntityID)
    }
}