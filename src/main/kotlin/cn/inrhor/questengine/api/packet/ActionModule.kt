package cn.inrhor.questengine.api.packet

class ActionModule(
    val type: PacketActionType,
    val set: List<String>,
    val trigger: List<String>,
    val pass: List<String>,
        val ratioEnable: Boolean) {
}

enum class PacketActionType {
    COLLECTION
}

fun String.toPacketAction(): PacketActionType {
    return when (this.uppercase()) {
        else -> PacketActionType.COLLECTION
    }
}