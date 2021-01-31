package cn.inrhor.questengine.api.nms.impl

import cn.inrhor.questengine.api.nms.NMS
import io.izzel.taboolib.module.lite.SimpleEquip
import net.minecraft.server.v1_16_R1.*
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.*

class NMSImpl : NMS() {

    override fun spawnAS(player: Player, entityId: Int, uuid: UUID, location: Location) {
        sendPacket(
            player,
            PacketPlayOutSpawnEntity(),
            "a" to entityId,
            "b" to uuid,
            "c" to location.x,
            "d" to location.y,
            "e" to location.z,
            "k" to EntityTypes.ARMOR_STAND
        )
    }

    override fun initAS(player: Player, entityId: Int, isSmall: Boolean, marker: Boolean) {
        updateEntityMetadata(player, entityId,
            getMetaEntityCustomNameVisible(true),
            getMetaEntitySilenced(true),
            getMetaEntityGravity(false),
            getMetaASProperties(isSmall, marker))
    }

    override fun spawnItem(player: Player, entityId: Int, uuid: UUID, location: Location, itemStack: ItemStack) {
        sendPacket(
            player,
            PacketPlayOutSpawnEntity(),
            "a" to entityId,
            "b" to uuid,
            "c" to location.x,
            "d" to location.y,
            "e" to location.z,
            "k" to EntityTypes.ITEM
        )
        updateEntityMetadata(player, entityId, getMetaEntityGravity(true), getMetaEntityItemStack(itemStack))
    }

    override fun destroyEntity(player: Player, entityId: Int) {
        sendPacket(player, PacketPlayOutEntityDestroy(entityId))
    }

    override fun updateEquipmentItem(player: Player, entityId: Int, itemStack: ItemStack) {
        if (version >= 11600) {
            sendPacket(
                player,
                PacketPlayOutEntityEquipment(
                    entityId,
                    listOf(
                        com.mojang.datafixers.util.Pair(
                            EnumItemSlot.fromName(
                                SimpleEquip.fromBukkit(EquipmentSlot.HEAD).nms),
                            CraftItemStack.asNMSCopy(itemStack)))
                )
            )
        }
    }

    override fun updatePassengers(player: Player, entityId: Int, vararg passengers: Int) {
        sendPacket(
            player,
            PacketPlayOutMount(),
            "a" to entityId,
            "b" to passengers)
    }

    override fun updateEntityMetadata(player: Player, entityId: Int, vararg objects: Any) {
        sendPacket(
            player,
            PacketPlayOutEntityMetadata(),
            "a" to entityId,
            "b" to objects.map { it as DataWatcher.Item<*> }.toList())
    }

    override fun getMetaEntityItemStack(itemStack: ItemStack): Any {
        return DataWatcher.Item(DataWatcherObject(7, DataWatcherRegistry.g), CraftItemStack.asNMSCopy(itemStack))
    }

    override fun getMetaASProperties(isSmall: Boolean, marker: Boolean): Any {
        var bytes = 0
        bytes += if (isSmall) 0x01 else 0
        bytes += 0x08
        bytes += if (marker) 0x10 else 0
        return DataWatcher.Item(DataWatcherObject(14, DataWatcherRegistry.a), bytes.toByte())
    }

    override fun getMetaEntityGravity(noGravity: Boolean): Any {
        return DataWatcher.Item(DataWatcherObject(5, DataWatcherRegistry.i), noGravity)
    }

    override fun getMetaEntitySilenced(silenced: Boolean): Any {
        return DataWatcher.Item(DataWatcherObject(4, DataWatcherRegistry.i), silenced)
    }

    override fun getMetaEntityCustomNameVisible(visible: Boolean): Any {
        return DataWatcher.Item(DataWatcherObject(3, DataWatcherRegistry.i), visible)
    }

    override fun getMetaEntityCustomName(name: String): Any {
        return DataWatcher.Item<Optional<IChatBaseComponent>>(DataWatcherObject(2, DataWatcherRegistry.f), Optional.of(ChatComponentText(name)))
    }

    override fun updateDisplayName(player: Player, entityId: Int, name: String) {
        updateEntityMetadata(player, entityId, getMetaEntityCustomName(name))
    }

    override fun updateLocation(player: Player, entityId: Int, location: Location) {
        sendPacket(
            player,
            PacketPlayOutEntityTeleport(),
            "a" to entityId,
            "b" to location.x,
            "c" to location.y,
            "d" to location.z,
            "g" to false
        )
    }
}