package cn.inrhor.questengine.common.nms

import cn.inrhor.questengine.common.nms.DataSerializerUtil.createDataSerializer
import net.minecraft.core.IRegistry
import net.minecraft.world.entity.EntityTypes
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.PacketDataSerializer
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata
import net.minecraft.network.syncher.DataWatcher

class NMS1193Impl: NMS1193() {

    override fun entityTypeGetId(any: Any): Int {
        val ir = BuiltInRegistries.ENTITY_TYPE as IRegistry<EntityTypes<*>>
        return ir.getId(any as EntityTypes<*>)
    }

    override fun packetPlayOutEntityMetadata(entityId: Int, objects: List<Any>): Any {
        return PacketPlayOutEntityMetadata(entityId, objects.map { (it as DataWatcher.Item<*>).value() })
    }

}