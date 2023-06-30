package cn.inrhor.questengine.common.nms

import cn.inrhor.questengine.common.nms.DataSerializerUtil.createDataSerializer
import net.minecraft.network.PacketDataSerializer
import net.minecraft.network.protocol.game.PacketPlayOutEntity
import net.minecraft.server.v1_16_R1.*
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common5.cbyte
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.library.reflex.Reflex.Companion.unsafeInstance
import taboolib.module.chat.colored
import taboolib.module.nms.MinecraftVersion
import taboolib.platform.compat.replacePlaceholder
import java.util.*
import taboolib.module.nms.*


class NMSImpl : NMS() {

    fun returnTypeNMS(type: String): EntityTypes<*> {
        when (type.uppercase()) {
            "ITEM" -> EntityTypes.ITEM
        }
        return EntityTypes.ARMOR_STAND
    }

    fun returnInt(type: String): Int {
        return type.toInt()
    }

    private val version = MinecraftVersion.major

    private val minor = MinecraftVersion.minor

    private val majorLegacy = MinecraftVersion.majorLegacy

    private val isUniversal = MinecraftVersion.isUniversal

    private fun packetSend(players: MutableSet<Player>, packet: Any, vararg fields: Pair<String, Any>) {
        players.forEach{
            it.sendPacket(setFields(packet, *fields))
        }
    }

    private fun packetSend(player: Player, packet: Any, vararg fields: Pair<String, Any?>) {
        player.sendPacket(setFields(packet, *fields))
    }

    private fun setFields(any: Any, vararg fields: Pair<String, Any?>): Any {
        fields.forEach { (key, value) ->
            if (value != null) {
                any.setProperty(key, value)
            }
        }
        return any
    }

    private fun getEntityType(entityTypes: String): Any {
        return net.minecraft.server.v1_16_R1.EntityTypes::class.java.getProperty<Any>(entityTypes.uppercase(), isStatic = true)!!
    }

    override fun spawnEntity(players: MutableSet<Player>, entityId: Int, entityType: String, location: Location) {
        if (isUniversal) {
            if (version > 10) {
                val yaw = (location.yaw * 256.0f / 360.0f).toInt().toByte()
                val pitch = (location.pitch * 256.0f / 360.0f).toInt().toByte()
                packetSend(
                    players,
                    net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity(
                        createDataSerializer {
                            writeVarInt(entityId)
                            writeUUID(UUID.randomUUID())
                            when (version) {
                                12 -> {
                                    writeVarInt(NMS1193.INSTANCE.entityTypeGetId(getEntityType(entityType)))
                                }
                                else -> {
                                    when (minor) {
                                        0, 1, 2 -> writeVarInt(Class.forName("net.minecraft.core.IRegistry").getProperty<Any>("ENTITY_TYPE", isStatic = true)!!.invokeMethod<Int>("getId", getEntityType(entityType) as net.minecraft.world.entity.EntityTypes<*>)!!)
                                        3 -> writeVarInt(NMS1193.INSTANCE.entityTypeGetId(getEntityType(entityType)))
                                    }
                                }
                            }
                            writeDouble(location.x)
                            writeDouble(location.y)
                            writeDouble(location.z)
                            writeByte(pitch)
                            writeByte(yaw)
                            writeByte(yaw)
                            writeVarInt(0)
                            writeShort(0)
                            writeShort(0)
                            writeShort(0)
                        }.build() as PacketDataSerializer
                    )
                )
            }else {
                packetSend(
                    players,
                    PacketPlayOutSpawnEntity::class.java.unsafeInstance(),
                    "id" to entityId,
                    "uuid" to UUID.randomUUID(),
                    "x" to location.x,
                    "y" to location.y,
                    "z" to location.z,
                    "type" to if (version >= 6) returnTypeNMS(entityType) else returnInt(
                        entityType
                    ),
                    "data" to 0
                )
            }
        } else {
            packetSend(
                players,
                PacketPlayOutSpawnEntity(),
                "a" to entityId,
                "b" to UUID.randomUUID(),
                "c" to location.x,
                "d" to location.y,
                "e" to location.z,
                "k" to if (version >= 6) returnTypeNMS(entityType) else returnInt(
                    entityType
                )
            )
        }
    }

    override fun spawnAS(players: MutableSet<Player>, entityId: Int, location: Location) {
        spawnEntity(players, entityId, "ARMOR_STAND", location)
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
        spawnEntity(players, entityId, "ITEM", location)
        updateEntityMetadata(players, entityId,
            getMetaEntityGravity(false),
            getMetaEntityItemStack(itemStack))
    }

    override fun destroyEntity(player: Player, entityId: Int) {
        if (isUniversal) {
            packetSend(
                player,
                net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy(entityId)
            )
        }else {
            packetSend(
                player,
                PacketPlayOutEntityDestroy(entityId)
            )
        }
    }

    override fun destroyEntity(players: MutableSet<Player>, entityId: Int) {
        players.forEach {
            destroyEntity(it, entityId)
        }
    }

    override fun updateEquipmentItem(players: MutableSet<Player>, entityId: Int, slot: EquipmentSlot, zitemStack: ItemStack) {
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
                    CraftItemStack.asNMSCopy(zitemStack)))
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
                    org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack.asNMSCopy(zitemStack)
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
        if (majorLegacy >= 11903) {
            packetSend(
                players,
                NMS1193.INSTANCE.packetPlayOutEntityMetadata(entityId, objects.toList())
            )
        }else if (isUniversal) {
            packetSend(players,
                net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata(
                    createDataSerializer {
                        writeVarInt(entityId)
                        writeMetadata(objects.map { it as net.minecraft.network.syncher
                            .DataWatcher.Item<*> }.toList())
                    }.build() as PacketDataSerializer,
                ))
        }else {
            packetSend(
                players,
                PacketPlayOutEntityMetadata::class.java.unsafeInstance(),
                "id" to entityId,
                "packedItems" to objects.map { it as DataWatcher.Item<*> }.toList()
            )
        }
    }

    override fun entityRotation(players: MutableSet<Player>, entityId: Int, yaw: Float) {
        val a = ((yaw%360)*256/360).cbyte
        if (isUniversal) {
            packetSend(
                players,
                PacketPlayOutEntity.PacketPlayOutEntityLook::class.java.unsafeInstance(),
                "entityId" to entityId,
                "yRot" to a
            )
        }else {
            packetSend(
                players,
                net.minecraft.server.v1_12_R1.PacketPlayOutEntity.PacketPlayOutEntityLook::class
                    .java.unsafeInstance(),
                "a" to entityId,
                "e" to a
            )
        }
    }

    /*
        这 updateEntityMetadata 有毒
     */
    override fun updateEntityMetadata(player: Player, entityId: Int, vararg objects: Any) {
        if (majorLegacy >= 11903) {
            packetSend(
                player,
                NMS1193.INSTANCE.packetPlayOutEntityMetadata(entityId, objects.toList())
            )
        }else if (isUniversal) {
            packetSend(player,
                net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata(
                    createDataSerializer {
                        writeVarInt(entityId)
                        writeMetadata(objects.map { it as net.minecraft.network.syncher
                        .DataWatcher.Item<*> }.toList())
                    }.build() as PacketDataSerializer,
                ))
        }else {
            packetSend(
                player,
                PacketPlayOutEntityMetadata::class.java.unsafeInstance(),
                "id" to entityId,
                "packedItems" to objects.map { it as DataWatcher.Item<*> }.toList()
            )
        }
    }

    override fun getMetaEntityItemStack(itemStack: ItemStack): Any {
        val index = if (version >= 9) 8 else if (version >= 6) 7 else 6
        return when {
            version > 10 -> net.minecraft.network.syncher.DataWatcher.Item(
                net.minecraft.network.syncher.DataWatcherObject(index, net.minecraft.network.syncher
                    .DataWatcherRegistry.ITEM_STACK), org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack
                        .asNMSCopy(itemStack)
            )
            version >= 5 -> DataWatcher.Item(DataWatcherObject(index,
                DataWatcherRegistry.g),
                CraftItemStack.asNMSCopy(itemStack))
            else -> net.minecraft.server.v1_12_R1.DataWatcher.Item(
                net.minecraft.server.v1_12_R1.DataWatcherObject(index,
                    net.minecraft.server.v1_12_R1.DataWatcherRegistry.f
                ), org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(itemStack)
            )
        }
    }

    override fun getMetaASProperties(isSmall: Boolean, marker: Boolean): Any {
        var bytes = 0
        bytes += if (isSmall) 0x01 else 0
        bytes += 0x08
        bytes += if (marker) 0x10 else 0
        return DataWatcher.Item(DataWatcherObject(getPropertiesIndex(), DataWatcherRegistry.a), bytes.toByte())
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
        return if (version > 10) {
            net.minecraft.network.syncher.DataWatcher.Item(
                net.minecraft.network.syncher.DataWatcherObject(index, net.minecraft.network.syncher
                    .DataWatcherRegistry.BOOLEAN), value
            )
        }else if (version >= 5) {
            DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.i), value)
        }else {
            net.minecraft.server.v1_11_R1.DataWatcher.Item(
                net.minecraft.server.v1_11_R1.DataWatcherObject(index,
                    net.minecraft.server.v1_11_R1.DataWatcherRegistry.h), value)
        }
    }

    override fun getMetaEntityCustomName(name: String): Any {
        return if (version > 10) {
            net.minecraft.network.syncher.DataWatcher.Item(
                net.minecraft.network.syncher
                    .DataWatcherObject<Optional<net.minecraft.network.chat.IChatBaseComponent>>(2,
                    net.minecraft.network.syncher.DataWatcherRegistry.OPTIONAL_COMPONENT),
                Optional.ofNullable(org.bukkit.craftbukkit.v1_19_R2.util.CraftChatMessage.fromString(name)[0]))
        }else if (version >= 5) {
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

    override fun camera(player: Player, entityId: Int) {
        if (isUniversal) {
            packetSend(
                player,
                PacketPlayOutCamera::class.java.unsafeInstance(),
                "a" to entityId
            )
        }else {
            packetSend(
                player,
                PacketPlayOutCamera(),
                "a" to entityId
            )
        }
    }

}