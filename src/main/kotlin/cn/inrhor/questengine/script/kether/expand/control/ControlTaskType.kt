package cn.inrhor.questengine.script.kether.expand.control

object ControlTaskType {
    fun returnType(str: String): ControlType {
        val i = str.lowercase().split(" ")
        return when(i[0]) {
            "packet send", "packet remove" -> ControlType.ASY
            else -> ControlType.SYN
        }
    }
}

enum class ControlType {
    SYN,
    ASY
}