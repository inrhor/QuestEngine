package cn.inrhor.questengine.api.nms

import io.izzel.taboolib.Version
import io.izzel.taboolib.kotlin.Reflex
import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.module.packet.TPacketHandler
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.*

abstract class NMS {

    abstract fun spawnAS(player: Player, entityId: Int, uuid: UUID, location: Location)

    abstract fun spawnItem(player: Player, entityId: Int, uuid: UUID, location: Location, itemStack: ItemStack)

    abstract fun destroyAS(player: Player, entityId: Int)

    abstract fun getMetaEntityInt(index: Int, value: Int): Any

    abstract fun updateDisplayName(player: Player, entityId: Int, name: String)

    abstract fun updateLocation(player: Player, entityId: Int, location: Location)

    abstract fun updatePassengers(player: Player, entityId: Int, vararg passengers: Int)

    abstract fun updateEquipmentItem(player: Player, entityId: Int, slot: EquipmentSlot, itemStack: ItemStack)

    abstract fun updateEntityMetadata(player: Player, entityId: Int, vararg objects: Any)

    abstract fun getMetaEntityItemStack(itemStack: ItemStack): Any

    abstract fun getMetaEntityProperties(onFire: Boolean, crouched: Boolean, sprinting: Boolean, swimming: Boolean, invisible: Boolean, glowing: Boolean, flyingElytra: Boolean): Any

    abstract fun getMetaEntityGravity(noGravity: Boolean): Any

    abstract fun getMetaEntitySilenced(silenced: Boolean): Any

    abstract fun getMetaEntityCustomNameVisible(visible: Boolean): Any

    abstract fun getMetaEntityCustomName(name: String): Any

    abstract fun sendEntityMetadata(player: Player, entityId: Int, vararg objects: Any)

    companion object {

        @TInject(asm = "cn.inrhor.questengine.api.nms.impl.NMSImpl")
        lateinit var INSTANCE: NMS
        internal val version = Version.getCurrentVersionInt()

        fun sendPacket(player: Player, packet: Any, vararg fields: Pair<String, Any>) {
            TPacketHandler.sendPacket(player, setFields(packet, *fields))
        }
        fun setFields(any: Any, vararg fields: Pair<String, Any>): Any {
            fields.forEach { (key, value) ->
                Reflex.from(any.javaClass, any).set(key, value)
            }
            return any
        }
    }
}