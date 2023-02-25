package cn.inrhor.questengine.common.nms

import taboolib.common.util.unsafeLazy
import taboolib.module.nms.nmsProxy

abstract class NMS1193 {

    /**
     * 1.19.3 获取实体类型
     */
    abstract fun entityTypeGetId(any: Any): Int

    abstract fun packetPlayOutEntityMetadata(entityId: Int, objects: List<Any>): Any

    companion object {
        @JvmStatic
        val INSTANCE by unsafeLazy { nmsProxy<NMS1193>() }
    }

}