package cn.inrhor.questengine.utlis.location

import cn.inrhor.questengine.utlis.variableReader

class ReferLocation {
    var offset: Float = 0.0f
    var multiply: Double = 0.0
    var height: Double = 0.0

    fun offsetType(type: String) {
        offset = LocationTool.getOffsetType(type)
    }
}

inline fun builderReferLoc(builder: ReferLocation.() -> Unit = {}): ReferLocation {
    return ReferLocation().also(builder)
}

fun builderReferLoc(str: String): ReferLocation {
    return builderReferLoc {
        str.variableReader().forEach { s ->
            val us = s.lowercase()
            if (us.contains("dir")) offsetType(s.replace("dir", "", true))
            if (us.contains("add")) {
                val sp = s.split(" ")
                multiply = sp[1].toDouble()
                height = sp[2].toDouble()
            }
        }
    }
}