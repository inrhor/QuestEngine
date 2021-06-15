package cn.inrhor.questengine.api.hologram

/**
 * Manager > HoloID & EntityID
 * 为了注册时自动给定EntityID
 */
class HoloIDManager {
    companion object {
        private val holoEntityIDs: MutableSet<Int> = mutableSetOf()
    }

    /**
     * type > text, item, itemStack
     */
    fun generate(dialogID: String, index: Int, type: String): Int {
        return "$dialogID-dialog-$index-$type".hashCode()
    }

    fun generate(dialogID: String, replyID: String, index: Int, type: String): Int {
        return "$dialogID-reply-$replyID-$index-$type".hashCode()
    }

    fun existEntityID(holoEntityID: Int): Boolean {
        return holoEntityIDs.contains(holoEntityID)
    }

    fun addEntityID(holoEntityID: Int) {
        holoEntityIDs.add(holoEntityID)
    }
}