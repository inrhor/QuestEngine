package cn.inrhor.questengine.common.nms.impl

import cn.inrhor.questengine.common.nms.NMS
import cn.inrhor.questengine.utlis.public.MsgUtil
import io.izzel.taboolib.module.lite.SimpleEquip
import io.izzel.taboolib.module.locale.TLocale
import net.minecraft.server.v1_16_R1.*
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.*

class NMSImpl : NMS() {

    override fun spawnAS(players: MutableSet<Player>, entityId: Int, location: Location) {
        sendPacket(
            players,
            PacketPlayOutSpawnEntity(),
            "a" to entityId,
            "b" to UUID.randomUUID(),
            "c" to location.x,
            "d" to location.y,
            "e" to location.z,
            "k" to EntityTypes.ARMOR_STAND
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
            "k" to EntityTypes.ITEM
        )
        updateEntityMetadata(players, entityId, getMetaEntityGravity(true), getMetaEntityItemStack(itemStack))
    }

    override fun destroyEntity(player: Player, entityId: Int) {
        sendPacket(player, PacketPlayOutEntityDestroy(entityId))
    }

    override fun updateEquipmentItem(players: MutableSet<Player>, entityId: Int, itemStack: ItemStack) {
        if (version >= 11600) {
            sendPacket(
                players,
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
        var colorName = TLocale.Translate.setColored(name)
        MsgUtil.send("empty ?  "+colorName.isEmpty())
        if (colorName.isEmpty()) colorName = " "
        updateEntityMetadata(player, entityId, getMetaEntityCustomName(
            TLocale.Translate.setPlaceholders(player, colorName)
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