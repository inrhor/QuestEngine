package cn.inrhor.questengine.common.nms

import taboolib.module.nms.DataSerializer
import taboolib.module.nms.DataSerializerFactory

object DataSerializerUtil {
    fun createDataSerializer(builder: DataSerializer.() -> Unit): DataSerializer {
        return DataSerializerFactory.instance.newSerializer().also(builder)
    }
}