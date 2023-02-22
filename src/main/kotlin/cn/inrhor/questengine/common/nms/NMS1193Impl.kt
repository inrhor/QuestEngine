package cn.inrhor.imipetcore.common.nms

import net.minecraft.core.IRegistry
import net.minecraft.world.entity.EntityTypes
import net.minecraft.core.registries.BuiltInRegistries

class NMS1193Impl: NMS1193() {

    override fun entityTypeGetId(any: Any): Int {
        val ir = BuiltInRegistries.ENTITY_TYPE as IRegistry<EntityTypes<*>>
        return ir.getId(any as EntityTypes<*>)
    }

}