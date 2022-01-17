package cn.inrhor.questengine.common.nms

import taboolib.module.nms.MinecraftVersion

fun getPropertiesIndex(): Int {
    return when (MinecraftVersion.major) {
        4, 5 -> 11
        6 -> 13
        7, 8 -> 14
        else -> 15
    }
}