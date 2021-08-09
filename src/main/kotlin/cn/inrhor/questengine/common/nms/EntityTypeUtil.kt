package cn.inrhor.questengine.common.nms

import net.minecraft.server.v1_16_R3.EntityTypes
import taboolib.module.nms.MinecraftVersion
import java.util.*

object EntityTypeUtil {

    fun returnTypeNMS(type: String): EntityTypes<*> {
        when (type.uppercase()) {
            "ITEM" -> EntityTypes.ITEM
        }
        return EntityTypes.ARMOR_STAND
    }

    fun returnInt(type: String): Int {
        return type.toInt()
    }

}

fun getPropertiesIndex(): Int {
    return when (MinecraftVersion.major) {
        1 -> 10
        2, 3, 4, 5 -> 11
        6 -> 13
        7, 8 -> 14
        else -> 15
    }
}