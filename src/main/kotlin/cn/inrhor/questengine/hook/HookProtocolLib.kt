package cn.inrhor.questengine.hook

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.MinecraftVersion
import java.util.*

object HookProtocolLib {

    fun sendPacket(player: Player, packet: PacketContainer) {
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
    }

    fun sendPacket(players: MutableSet<Player>, packet: PacketContainer) {
        players.forEach {
            sendPacket(it, packet)
        }
    }

    val version = MinecraftVersion.major

    fun spawnEntity(players: MutableSet<Player>, entityId: Int, entityType: String, location: Location) {
        val packet = PacketContainer(PacketType.Play.Server.SPAWN_ENTITY)
        packet.modifier.writeDefaults()
        packet.integers.write(0, entityId)
        packet.uuiDs.write(0, UUID.randomUUID())
        if (version >= 5) {
            packet.entityTypeModifier.write(0, EntityType.valueOf(entityType.lowercase(Locale.getDefault())))
        }else {
            packet.integers.write(6, entityType.toInt())
        }
        packet.doubles
            .write(0, location.x)
            .write(1, location.y)
            .write(2, location.z)

        sendPacket(players, packet)
    }

    fun spawnAS(players: MutableSet<Player>, entityId: Int, location: Location) {
        val entityType = if (version >= 5) "armor_stand" else "78"
        spawnEntity(players, entityId, entityType, location)
    }

    fun spawnItem(players: MutableSet<Player>, entityId: Int, location: Location, itemStack: ItemStack) {
        val entityType = if (version >= 5) "item" else "2"
        spawnEntity(players, entityId, entityType, location)
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)
        packet.modifier.writeDefaults()
        packet.integers.write(0, entityId)
        val metadata = WrappedDataWatcher()
        setEntityGravity(metadata, true)
        setEntityItemStack(metadata, itemStack)
        packet.watchableCollectionModifier.write(0, metadata.watchableObjects)
        sendPacket(players, packet)
    }

    fun destroyEntity(player: Player, entityId: Int) {
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)
        packet.modifier.writeDefaults()
        packet.integers.write(1, entityId)
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
        packet.integers.write(0, entityId)
        packet.slotStackPairLists.write(
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
        packet.integers.write(0, entityId)
        packet.integerArrays.write(0, passengers)
        sendPacket(players, packet)
    }

    fun initAS(players: MutableSet<Player>, entityId: Int, showName: Boolean, isSmall: Boolean, marker: Boolean) {
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)
        packet.modifier.writeDefaults()
        packet.integers.write(0, entityId)
        val metadata = WrappedDataWatcher()
        setEntityCustomNameVisible(metadata, showName)
        setEntitySilent(metadata, true)
        setEntityGravity(metadata, false)
        setMetaASProperties(metadata, isSmall, marker)
        setIsInvisible(metadata)
        packet.watchableCollectionModifier.write(0, metadata.watchableObjects)
        sendPacket(players, packet)
    }

    fun setEntityItemStack(metadata: WrappedDataWatcher, itemStack: ItemStack) {
        metadata.setObject(
            WrappedDataWatcherObject(
                0,
                WrappedDataWatcher.Registry.getItemStackSerializer(false)
            ), itemStack
        )
    }

    fun setIsInvisible(metadata: WrappedDataWatcher) {
        setMetaBytes(metadata, 0, 0x20.toByte())
    }

    fun setEntityGravity(metadata: WrappedDataWatcher, noGravity: Boolean) {
        setMetaBoolean(metadata, 5, noGravity)
    }

    fun setEntitySilent(metadata: WrappedDataWatcher, silent: Boolean) {
        setMetaBoolean(metadata, 4, silent)
    }

    fun setEntityCustomNameVisible(metadata: WrappedDataWatcher, visible: Boolean) {
        setMetaBoolean(metadata, 3, visible)
    }

    fun setEntityCustomNameVisible(players: MutableSet<Player>, entityId: Int, visible: Boolean) {
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)
        packet.integers.write(0, entityId)
        val metadata = WrappedDataWatcher()
        setEntityCustomNameVisible(metadata, visible)
        packet.watchableCollectionModifier.write(0, metadata.watchableObjects)
        sendPacket(players, packet)
    }

    fun isInvisible(players: MutableSet<Player>, entityId: Int) {
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)
        packet.integers.write(0, entityId)
        val metadata = WrappedDataWatcher()
        setIsInvisible(metadata)
        packet.watchableCollectionModifier.write(0, metadata.watchableObjects)
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

    fun setMetaASProperties(metadata: WrappedDataWatcher, isSmall: Boolean, marker: Boolean) {
        if (isSmall) setMetaBytes(metadata, 14, (0x08 or 0x01).toByte()) // no BasePlate & small
        if (marker) setMetaBytes(metadata, 14, 0x10.toByte())
    }

    fun setEntityCustomName(metadata: WrappedDataWatcher, name: String) {
        val opt: Optional<*> = Optional.of(
            WrappedChatComponent.fromChatMessage(name)[0].handle
        )
        metadata.setObject(
            WrappedDataWatcherObject(
                5, // 对应 https://wiki.vg/Entity_metadata#Entity_Metadata_Format 的 Index
                WrappedDataWatcher.Registry.getChatComponentSerializer(true)), opt)
    }

    fun updateDisplayName(players: MutableSet<Player>, entityId: Int, name: String) {
        players.forEach {
            updateDisplayName(it, entityId, name)
        }
    }

    fun updateDisplayName(player: Player, entityId: Int, name: String) {
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)
        packet.modifier.writeDefaults()
        packet.integers.write(0, entityId)
        val metadata = WrappedDataWatcher()
        setEntityCustomName(metadata, name)
        packet.watchableCollectionModifier.write(0, metadata.watchableObjects)
        sendPacket(player, packet)
    }

    fun updateLocation(players: MutableSet<Player>, entityId: Int, location: Location) {
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT)
        packet.modifier.writeDefaults()
        packet.integers.write(0, entityId)
        packet.doubles
            .write(0, location.x)
            .write(1, location.y)
            .write(2, location.z)
        sendPacket(players, packet)
    }


}