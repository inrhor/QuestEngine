package cn.inrhor.questengine.common.nms

import it.unimi.dsi.fastutil.ints.IntLists
import net.minecraft.server.v1_16_R1.*
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common.reflect.Reflex.Companion.setProperty
import taboolib.common.reflect.Reflex.Companion.unsafeInstance
import taboolib.module.chat.colored
import taboolib.module.nms.MinecraftVersion
import taboolib.platform.compat.replacePlaceholder
import java.util.*
import taboolib.module.nms.*


class NMSImpl : NMS() {

    val version = MinecraftVersion.major

    val isUniversal = MinecraftVersion.isUniversal

    fun packetSend(players: MutableSet<Player>, packet: Any, vararg fields: Pair<String, Any>) {
        players.forEach{
            it.sendPacket(setFields(packet, *fields))
        }
    }

    fun packetSend(player: Player, packet: Any, vararg fields: Pair<String, Any?>) {
        player.sendPacket(setFields(packet, *fields))
    }

    fun setFields(any: Any, vararg fields: Pair<String, Any?>): Any {
        fields.forEach { (key, value) ->
            if (value != null) {
                any.setProperty(key, value)
            }
        }
        return any
    }

    override fun spawnEntity(players: MutableSet<Player>, entityId: Int, entityType: String, location: Location) {
        if (isUniversal) {
            packetSend(
                players,
                PacketPlayOutSpawnEntity::class.java.unsafeInstance(),
                "id" to entityId,
                "uuid" to UUID.randomUUID(),
                "x" to location.x,
                "y" to location.y,
                "z" to location.z,
                "type" to EntityTypeUtil.returnTypeNMS(entityType),
                "data" to 0
            )
        } else {
            packetSend(
                players,
                PacketPlayOutSpawnEntity(),
                "a" to entityId,
                "b" to UUID.randomUUID(),
                "c" to location.x,
                "d" to location.y,
                "e" to location.z,
                "k" to if (version >= 5) EntityTypeUtil.returnTypeNMS(entityType) else EntityTypeUtil.returnInt(
                    entityType
                )
            )
        }
    }

    override fun spawnAS(players: MutableSet<Player>, entityId: Int, location: Location) {
        if (isUniversal) {
            packetSend(
                players,
                PacketPlayOutSpawnEntity::class.java.unsafeInstance(),
                "id" to entityId,
                "uuid" to UUID.randomUUID(),
                "x" to location.x,
                "y" to location.y,
                "z" to location.z,
                "type" to EntityTypes.ARMOR_STAND,
                "data" to 0
            )
        } else {
            packetSend(
                players,
                PacketPlayOutSpawnEntity(),
                "a" to entityId,
                "b" to UUID.randomUUID(),
                "c" to location.x,
                "d" to location.y,
                "e" to location.z,
                "k" to if (version >= 5) EntityTypes.ARMOR_STAND else 78
            )
        }
    }

    override fun initAS(players: MutableSet<Player>, entityId: Int, showName: Boolean, isSmall: Boolean, marker: Boolean) {
        updateEntityMetadata(
            players, entityId,
            getMetaEntityCustomNameVisible(showName),
            getMetaEntitySilenced(true),
            getMetaEntityGravity(false),
            getMetaASProperties(isSmall, marker),
            getIsInvisible()
        )
    }

    override fun spawnItem(players: MutableSet<Player>, entityId: Int, location: Location, itemStack: ItemStack) {
        if (isUniversal) {
            packetSend(
                players,
                PacketPlayOutSpawnEntity::class.java.unsafeInstance(),
                "id" to entityId,
                "uuid" to UUID.randomUUID(),
                "x" to location.x,
                "y" to location.y,
                "z" to location.z,
                "type" to EntityTypes.ITEM,
                "data" to 0
            )
        } else {
            packetSend(
                players,
                PacketPlayOutSpawnEntity(),
                "a" to entityId,
                "b" to UUID.randomUUID(),
                "c" to location.x,
                "d" to location.y,
                "e" to location.z,
                "k" to if (version >= 5) EntityTypes.ITEM else 2
            )
            updateEntityMetadata(players, entityId, getMetaEntityGravity(true), getMetaEntityItemStack(itemStack))
        }
    }

    override fun destroyEntity(player: Player, entityId: Int) {
        if (isUniversal) {
            packetSend(
                player,
                PacketPlayOutEntityDestroy::class.java.unsafeInstance(),
                "entityIds" to IntLists.singleton(entityId)
            )
        } else {
            packetSend(player, PacketPlayOutEntityDestroy(entityId))
        }
    }

    override fun destroyEntity(players: MutableSet<Player>, entityId: Int) {
        players.forEach{
            destroyEntity(it, entityId)
        }
    }

    override fun updateEquipmentItem(players: MutableSet<Player>, entityId: Int, slot: EquipmentSlot, itemStack: ItemStack) {
        if (version >= 8) {
            packetSend(
                players,
                PacketPlayOutEntityEquipment(
                    entityId,
                    listOf(com.mojang.datafixers.util.Pair(when (slot) {
                        EquipmentSlot.HAND -> EnumItemSlot.MAINHAND
                        EquipmentSlot.OFF_HAND -> EnumItemSlot.OFFHAND
                        EquipmentSlot.FEET -> EnumItemSlot.FEET
                        EquipmentSlot.LEGS -> EnumItemSlot.LEGS
                        EquipmentSlot.CHEST -> EnumItemSlot.CHEST
                        EquipmentSlot.HEAD -> EnumItemSlot.HEAD
                    },
                    CraftItemStack.asNMSCopy(itemStack)))
                )
            )
        }else if (version >= 1) {
            packetSend(
                players,
                net.minecraft.server.v1_13_R2.PacketPlayOutEntityEquipment(
                    entityId,
                    when (slot) {
                        EquipmentSlot.HAND -> net.minecraft.server.v1_13_R2.EnumItemSlot.MAINHAND
                        EquipmentSlot.OFF_HAND -> net.minecraft.server.v1_13_R2.EnumItemSlot.OFFHAND
                        EquipmentSlot.FEET -> net.minecraft.server.v1_13_R2.EnumItemSlot.FEET
                        EquipmentSlot.LEGS -> net.minecraft.server.v1_13_R2.EnumItemSlot.LEGS
                        EquipmentSlot.CHEST -> net.minecraft.server.v1_13_R2.EnumItemSlot.CHEST
                        EquipmentSlot.HEAD -> net.minecraft.server.v1_13_R2.EnumItemSlot.HEAD
                    },
                    org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack.asNMSCopy(itemStack)
                )
            )
        }
    }

    override fun updatePassengers(players: MutableSet<Player>, entityId: Int, vararg passengers: Int) {
        if (isUniversal) {
            packetSend(
                players,
                PacketPlayOutMount::class.java.unsafeInstance(),
                "vehicle" to entityId,
                "passengers" to passengers
            )
        } else {
            packetSend(
                players,
                PacketPlayOutMount(),
                "a" to entityId,
                "b" to passengers
            )
        }
    }

    override fun updateEntityMetadata(players: MutableSet<Player>, entityId: Int, vararg objects: Any) {
        if (isUniversal) {
            packetSend(
                players,
                PacketPlayOutEntityMetadata::class.java.unsafeInstance(),
                "id" to entityId,
                "packedItems" to objects.map { it as DataWatcher.Item<*> }.toList()
            )
        } else {
            packetSend(
                players,
                PacketPlayOutEntityMetadata(),
                "a" to entityId,
                "b" to objects.map { it as DataWatcher.Item<*> }.toList()
            )
        }
    }

    /*
        这 updateEntityMetadata 有毒
     */

    override fun updateEntityMetadata(player: Player, entityId: Int, vararg objects: Any) {
        if (isUniversal) {
            packetSend(
                player,
                PacketPlayOutEntityMetadata::class.java.unsafeInstance(),
                "id" to entityId,
                "packedItems" to objects.map { it as DataWatcher.Item<*> }.toList()
            )
        } else {
            packetSend(
                player,
                PacketPlayOutEntityMetadata(),
                "a" to entityId,
                "b" to objects.map { it as DataWatcher.Item<*> }.toList())
        }
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

    override fun getIsInvisible(): Any {
        return DataWatcher.Item(DataWatcherObject(0, DataWatcherRegistry.a), 0x20)
    }

    override fun getMetaEntityGravity(noGravity: Boolean): Any {
        return getMetaEntityValue(5, noGravity)
    }

    override fun getMetaEntitySilenced(silenced: Boolean): Any {
        return getMetaEntityValue(4, silenced)
    }

    override fun getMetaEntityCustomNameVisible(visible: Boolean): Any {
        return getMetaEntityValue(3, visible)
    }

    private fun getMetaEntityValue(index: Int, value: Boolean): Any {
        return if (version >= 5) {
            DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.i), value)
        }else {
            net.minecraft.server.v1_11_R1.DataWatcher.Item(
                net.minecraft.server.v1_11_R1.DataWatcherObject(index,
                    net.minecraft.server.v1_11_R1.DataWatcherRegistry.h), value)
        }
    }

    override fun getMetaEntityCustomName(name: String): Any {
        return if (version >= 5) {
            DataWatcher.Item<Optional<IChatBaseComponent>>(
                DataWatcherObject(2, DataWatcherRegistry.f),
                Optional.of(ChatComponentText(name)))
        } else {
            net.minecraft.server.v1_12_R1.DataWatcher.Item(
                net.minecraft.server.v1_12_R1.DataWatcherObject(2,
                    net.minecraft.server.v1_12_R1.DataWatcherRegistry.d), name)
        }
    }

    override fun updateDisplayName(players: MutableSet<Player>, entityId: Int, name: String) {
        players.forEach{
            updateDisplayName(it, entityId, name)
        }
    }

    override fun updateDisplayName(player: Player, entityId: Int, name: String) {
        var colorName = name.colored()
        if (colorName.isEmpty()) colorName = " "
        updateEntityMetadata(player, entityId, getMetaEntityCustomName(
            colorName.replacePlaceholder(player)
        ))
    }

    override fun updateLocation(players: MutableSet<Player>, entityId: Int, location: Location) {
        if (isUniversal) {
            packetSend(
                players,
                PacketPlayOutEntityTeleport::class.java.unsafeInstance(),
                "id" to entityId,
                "x" to location.x,
                "y" to location.y,
                "z" to location.z,
                "onGround" to false
            )
        } else {
            packetSend(
                players,
                PacketPlayOutEntityTeleport(),
                "a" to entityId,
                "b" to location.x,
                "c" to location.y,
                "d" to location.z,
                "g" to false
            )
        }
    }
}