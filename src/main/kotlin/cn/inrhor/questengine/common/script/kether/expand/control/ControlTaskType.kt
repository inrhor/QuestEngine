package cn.inrhor.questengine.common.script.kether.expand.control

import java.util.*

object ControlTaskType {
    fun returnType(str: String): ControlType {
        val i = str.lowercase(Locale.getDefault()).split(" ")
        return when(i[0]) {
            "send packet" -> ControlType.ASY
            else -> ControlType.SYN
        }
    }
}

enum class ControlType {
    SYN,
    ASY
}