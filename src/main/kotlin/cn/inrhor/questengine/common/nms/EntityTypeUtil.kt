package cn.inrhor.questengine.common.nms

import net.minecraft.server.v1_16_R3.EntityTypes
import java.util.*

object EntityTypeUtil {

    fun returnTypeNMS(type: String): EntityTypes<*> {
        when (type.uppercase(Locale.getDefault())) {
            "ITEM" -> EntityTypes.ITEM
        }
        return EntityTypes.ARMOR_STAND
    }

    fun returnInt(type: String): Int {
        return type.toInt()
    }

}