package cn.inrhor.questengine.hook

import cn.inrhor.questengine.common.nms.getPropertiesIndex
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject
import net.md_5.bungee.chat.ComponentSerializer
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.module.chat.colored
import taboolib.module.nms.MinecraftVersion
import java.util.*

object HookProtocolLib {

    private fun sendPacket(player: Player, packet: PacketContainer) {
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
    }

    private fun sendPacket(players: MutableSet<Player>, packet: PacketContainer) {
        players.forEach {
            sendPacket(it, packet)
        }
    }

    private val version = MinecraftVersion.major

    private val minor = MinecraftVersion.minor

    fun spawnEntity(players: MutableSet<Player>, entityId: Int, entityType: String, location: Location) {
        val packet = PacketContainer(PacketType.Play.Server.SPAWN_ENTITY)
        packet.modifier.writeDefaults()
        packet.integers.writeSafely(0, entityId)
        packet.uuiDs.writeSafely(0, UUID.randomUUID())
        when {
            version >= 6 -> {
                packet.entityTypeModifier.writeSafely(0, EntityType.valueOf(entityType.uppercase()))
            }
            else -> {
                packet.integers.writeSafely(6, entityType.toInt())
            }
        }
        packet.doubles
            .writeSafely(0, location.x)
            .writeSafely(1, location.y)
            .writeSafely(2, location.z)

        sendPacket(players, packet)
    }

    fun spawnAS(players: MutableSet<Player>, entityId: Int, location: Location) {
        val entityType = if (version >= 6) "ARMOR_STAND" else "78"
        spawnEntity(players, entityId, entityType, location)
    }

    fun spawnItem(players: MutableSet<Player>, entityId: Int, location: Location, itemStack: ItemStack) {
        val entityType = if (version >= 6) "DROPPED_ITEM" else "2"
        spawnEntity(players, entityId, entityType, location)
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)
        packet.modifier.writeDefaults()
        packet.integers.writeSafely(0, entityId)
        val metadata = WrappedDataWatcher()
        setEntityItemStack(metadata, itemStack)
        setEntityGravity(metadata, true)
        packet.watchableCollectionModifier.writeSafely(0, metadata.watchableObjects)
        sendPacket(players, packet)
    }

    fun destroyEntity(player: Player, entityId: Int) {
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)
        if (version >= 9 && minor == 0) {
            if (minor == 0) {
                packet.integers.writeSafely(0, entityId)
            }else {
                packet.intLists.writeSafely(0, listOf(entityId))
            }
        }else {
            packet.integerArrays.writeSafely(0, intArrayOf(entityId))
        }
        sendPacket(player, packet)
    }

    fun destroyEntity(players: MutableSet<Player>, entityId: Int) {
        players.forEach {
            destroyEntity(it, entityId)
        }
    }

    fun updateEquipmentItem(players: MutableSet<Player>, entityId: Int, slot: EquipmentSlot, itemStack: ItemStack) {
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT)
        packet.modifier.writeDefaults()
        packet.integers.writeSafely(0, entityId)
        packet.slotStackPairLists.writeSafely(
            0,
            Collections.singletonList(
                com.comphenix.protocol.wrappers.Pair(when (slot) {
                    EquipmentSlot.HAND -> ItemSlot.MAINHAND
                    EquipmentSlot.OFF_HAND -> ItemSlot.OFFHAND
                    EquipmentSlot.FEET -> ItemSlot.FEET
                    EquipmentSlot.LEGS -> ItemSlot.LEGS
                    EquipmentSlot.CHEST -> ItemSlot.CHEST
                    EquipmentSlot.HEAD -> ItemSlot.HEAD
                }, itemStack)))
        sendPacket(players, packet)
    }

    fun updatePassengers(players: MutableSet<Player>, entityId: Int, vararg passengers: Int) {
        val packet = PacketContainer(PacketType.Play.Server.MOUNT)
        packet.modifier.writeDefaults()
        packet.integers.writeSafely(0, entityId)
        packet.integerArrays.writeSafely(0, passengers)
        sendPacket(players, packet)
    }

    fun initAS(players: MutableSet<Player>, entityId: Int, showName: Boolean, isSmall: Boolean, marker: Boolean) {
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)
        packet.modifier.writeDefaults()
        packet.integers.writeSafely(0, entityId)
        val metadata = WrappedDataWatcher()
        setEntityCustomNameVisible(metadata, showName)
        setEntitySilent(metadata)
        setEntityGravity(metadata, false)
        setMetaASProperties(metadata, isSmall, marker)
        setIsInvisible(metadata)
        packet.watchableCollectionModifier.writeSafely(0, metadata.watchableObjects)
        sendPacket(players, packet)
    }

    private fun setEntityItemStack(metadata: WrappedDataWatcher, itemStack: ItemStack) {
        val index = if (version >= 9) 8 else if (version >= 6) 7 else if (version >= 4) 6 else 5
        metadata.setObject(
            WrappedDataWatcherObject(
                index,
                WrappedDataWatcher.Registry.getItemStackSerializer(false)
            ), itemStack
        )
    }

    private fun setIsInvisible(metadata: WrappedDataWatcher) {
        setMetaBytes(metadata, 0, 0x20.toByte())
    }

    private fun setEntityGravity(metadata: WrappedDataWatcher, noGravity: Boolean) {
        setMetaBoolean(metadata, 5, noGravity)
    }

    private fun setEntitySilent(metadata: WrappedDataWatcher) {
        setMetaBoolean(metadata, 4, true)
    }

    private fun setEntityCustomNameVisible(metadata: WrappedDataWatcher, visible: Boolean) {
        setMetaBoolean(metadata, 3, visible)
    }

    fun setEntityCustomNameVisible(players: MutableSet<Player>, entityId: Int, visible: Boolean) {
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)
        packet.integers.writeSafely(0, entityId)
        val metadata = WrappedDataWatcher()
        setEntityCustomNameVisible(metadata, visible)
        packet.watchableCollectionModifier.writeSafely(0, metadata.watchableObjects)
        sendPacket(players, packet)
    }

    fun isInvisible(players: MutableSet<Player>, entityId: Int) {
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)
        packet.integers.writeSafely(0, entityId)
        val metadata = WrappedDataWatcher()
        setIsInvisible(metadata)
        packet.watchableCollectionModifier.writeSafely(0, metadata.watchableObjects)
        sendPacket(players, packet)
    }

    private fun setMetaBoolean(metadata: WrappedDataWatcher, index: Int, open: Boolean) {
        metadata.setObject(
            WrappedDataWatcherObject(
                index,
                WrappedDataWatcher.Registry.get(Boolean::class.javaObjectType)
            ),
            open
        )
    }

    private fun setMetaBytes(metadata: WrappedDataWatcher, index: Int, byte: Byte) {
        metadata.setObject(
            WrappedDataWatcherObject(index,
                WrappedDataWatcher.Registry.get(Byte::class.javaObjectType)),
            byte)
    }

    private fun setMetaASProperties(metadata: WrappedDataWatcher, isSmall: Boolean, marker: Boolean) {
        val index = getPropertiesIndex()
        var bytes = 0
        bytes += if (isSmall) 0x01 else 0
        bytes += 0x08
        bytes += if (marker) 0x10 else 0
        setMetaBytes(metadata, index, bytes.toByte())
    }

    private fun setEntityCustomName(metadata: WrappedDataWatcher, name: String) {
        when {
            version >= 8 -> {
                metadata.setObject(
                    WrappedDataWatcherObject(2,
                        WrappedDataWatcher.Registry.getChatComponentSerializer(true)),
                    Optional.of(WrappedChatComponent.fromJson(ComponentSerializer.toString(name)).handle)
                )
            }
            version >= 6 -> {
                metadata.setObject(WrappedDataWatcherObject(2,
                    WrappedDataWatcher.Registry.getChatComponentSerializer(true)),
                    Optional.of(WrappedChatComponent.fromChatMessage(name)[0].handle)
                )
            }
            version >= 5 -> {
                metadata.setObject(WrappedDataWatcherObject(2,
                        WrappedDataWatcher.Registry.getChatComponentSerializer(true)),
                    Optional.of(WrappedChatComponent.fromText(name).handle)
                )
            }
            else -> {
                metadata.setObject(
                    WrappedDataWatcherObject(2,
                        WrappedDataWatcher.Registry.get(String::class.javaObjectType)),
                    name)
            }
        }

    }

    fun updateDisplayName(players: MutableSet<Player>, entityId: Int, name: String) {
        players.forEach {
            updateDisplayName(it, entityId, name)
        }
    }

    fun updateDisplayName(player: Player, entityId: Int, name: String) {
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)
        packet.modifier.writeDefaults()
        packet.integers.writeSafely(0, entityId)
        val metadata = WrappedDataWatcher()
        setEntityCustomName(metadata, name.colored())
        packet.watchableCollectionModifier.writeSafely(0, metadata.watchableObjects)
        sendPacket(player, packet)
    }

    fun updateLocation(players: MutableSet<Player>, entityId: Int, location: Location) {
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT)
        packet.modifier.writeDefaults()
        packet.integers.writeSafely(0, entityId)
        packet.doubles
            .writeSafely(0, location.x)
            .writeSafely(1, location.y)
            .writeSafely(2, location.z)
        sendPacket(players, packet)
    }


}