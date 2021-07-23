package cn.inrhor.questengine.script.kether.expand.control

import java.util.*

object ControlTaskType {
    fun returnType(str: String): ControlType {
        val i = str.lowercase(Locale.getDefault()).split(" ")
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