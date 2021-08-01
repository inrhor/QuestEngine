package cn.inrhor.questengine.common.nms.impl

import cn.inrhor.questengine.common.nms.EntityTypeUtil
import cn.inrhor.questengine.common.nms.NMS
import net.minecraft.server.v1_16_R1.*
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common.reflect.Reflex.Companion.setProperty
import taboolib.module.chat.colored
import taboolib.module.nms.MinecraftVersion
import taboolib.platform.compat.replacePlaceholder
import java.util.*

class NMSImpl : NMS() {

    private val version = MinecraftVersion.majorLegacy

    fun Player.sendPacket(packet: Any, vararg fields: Pair<String, Any?>) {
        sendPacket(setFields(packet, *fields))
    }

    fun sendPacket(players: MutableSet<Player>, packet: Any, vararg fields: Pair<String, Any>) {
        players.forEach{
            it.sendPacket(setFields(packet, *fields))
        }
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
        sendPacket(
            players,
            PacketPlayOutSpawnEntity(),
            "a" to entityId,
            "b" to UUID.randomUUID(),
            "c" to location.x,
            "d" to location.y,
            "e" to location.z,
            "k" to if (version >= 11400) EntityTypeUtil.returnTypeNMS(entityType) else EntityTypeUtil.returnInt(entityType)
        )
    }

    override fun spawnAS(players: MutableSet<Player>, entityId: Int, location: Location) {
        sendPacket(
            players,
            PacketPlayOutSpawnEntity(),
            "a" to entityId,
            "b" to UUID.randomUUID(),
            "c" to location.x,
            "d" to location.y,
            "e" to location.z,
            "k" to if (version >= 11400) EntityTypes.ARMOR_STAND else 78
        )
    }

    override fun initAS(players: MutableSet<Player>, entityId: Int, showName: Boolean, isSmall: Boolean, marker: Boolean) {
        updateEntityMetadata(players, entityId,
            getMetaEntityCustomNameVisible(showName),
            getMetaEntitySilenced(true),
            getMetaEntityGravity(false),
            getMetaASProperties(isSmall, marker),
            getIsInvisible())
    }

    override fun spawnItem(players: MutableSet<Player>, entityId: Int, location: Location, itemStack: ItemStack) {
        sendPacket(
            players,
            PacketPlayOutSpawnEntity(),
            "a" to entityId,
            "b" to UUID.randomUUID(),
            "c" to location.x,
            "d" to location.y,
            "e" to location.z,
            "k" to if (version >= 11400) EntityTypes.ITEM else 2
        )
        updateEntityMetadata(players, entityId, getMetaEntityGravity(true), getMetaEntityItemStack(itemStack))
    }

    override fun destroyEntity(player: Player, entityId: Int) {
        player.sendPacket(PacketPlayOutEntityDestroy(entityId))
    }

    override fun destroyEntity(players: MutableSet<Player>, entityId: Int) {
        players.forEach{
            destroyEntity(it, entityId)
        }
    }

    override fun updateEquipmentItem(players: MutableSet<Player>, entityId: Int, slot: EquipmentSlot, itemStack: ItemStack) {
        if (version >= 11600) {
            sendPacket(
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
        }
    }

    override fun updatePassengers(players: MutableSet<Player>, entityId: Int, vararg passengers: Int) {
        sendPacket(
            players,
            PacketPlayOutMount(),
            "a" to entityId,
            "b" to passengers)
    }

    override fun updateEntityMetadata(players: MutableSet<Player>, entityId: Int, vararg objects: Any) {
        sendPacket(
            players,
            PacketPlayOutEntityMetadata(),
            "a" to entityId,
            "b" to objects.map { it as DataWatcher.Item<*> }.toList())
    }

    override fun updateEntityMetadata(player: Player, entityId: Int, vararg objects: Any) {
        player.sendPacket(
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

    override fun getIsInvisible(): Any {
        return DataWatcher.Item(DataWatcherObject(0, DataWatcherRegistry.a), 0x20)
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
        sendPacket(
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