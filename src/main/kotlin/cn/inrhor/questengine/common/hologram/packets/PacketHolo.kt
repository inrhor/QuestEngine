package cn.inrhor.questengine.common.hologram.packets

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.Pair
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*


class PacketHolo {

    private fun sendServerPacket(player: Player, packet: PacketContainer) {
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
    }

    fun spawnAS(player: Player, entityID: Int, loc: Location) {
        val packet = PacketContainer(PacketType.Play.Server.SPAWN_ENTITY)
        packet.modifier.writeDefaults()

        packet.integers.write(0, entityID)
        packet.uuiDs.write(0, UUID.randomUUID())
        packet.entityTypeModifier.write(0, EntityType.ARMOR_STAND)
        packet.doubles.write(0, loc.x)
        packet.doubles.write(1, loc.y)
        packet.doubles.write(2, loc.z)

        sendServerPacket(player, packet)
    }

    fun setMetadata(player: Player, entityID: Int) {
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)
        packet.modifier.writeDefaults()
        packet.integers.write(0, entityID)

        val metadata = WrappedDataWatcher()

        // custom name visible
        metadata.setObject(
            WrappedDataWatcher.WrappedDataWatcherObject(
                3,
                WrappedDataWatcher.Registry.get(Boolean::class.javaObjectType)),
            true
        )
        // invisible
        metadata.setObject(
            WrappedDataWatcher.WrappedDataWatcherObject(
                0,
                WrappedDataWatcher.Registry.get(Byte::class.javaObjectType)),
            0x20.toByte()
        )
        // silent
        metadata.setObject(
            WrappedDataWatcher.WrappedDataWatcherObject(
                4,
                WrappedDataWatcher.Registry.get(Boolean::class.javaObjectType)),
            true
        )
        // no BasePlate & small
        metadata.setObject(
            WrappedDataWatcher.WrappedDataWatcherObject(
                14,
                WrappedDataWatcher.Registry.get(Byte::class.javaObjectType)),
            (0x08 or 0x01).toByte()
        )

        packet.watchableCollectionModifier.write(0, metadata.watchableObjects)

        sendServerPacket(player, packet)
    }

    fun setText(player: Player, entityID: Int, text: String) {
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)
        packet.modifier.writeDefaults()
        packet.integers.write(0, entityID)

        val metadata = WrappedDataWatcher()

        // custom name
        val opt: Optional<*> = Optional.of(
            WrappedChatComponent
                .fromChatMessage(text)[0].handle
        )
        metadata.setObject(
            WrappedDataWatcher.WrappedDataWatcherObject(
                2, // 对应 https://wiki.vg/Entity_metadata#Entity_Metadata_Format 的 Index
                WrappedDataWatcher.Registry.getChatComponentSerializer(
                    true)),
            opt
        )

        packet.watchableCollectionModifier.write(0, metadata.watchableObjects)

        sendServerPacket(player, packet)
    }

    fun setItem(player: Player, entityID: Int, item: ItemStack) {
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT)
        packet.integers.write(0, entityID)
        packet.slotStackPairLists.write(
            0,
            Collections.singletonList(
                Pair(
                    EnumWrappers.ItemSlot.HEAD,
                    item)
            ))

        sendServerPacket(player, packet)
    }

    /*
    重写
     */
    /*fun sendHolo(player: Player,
                 id: String,
                 loc: Location,
                 contents: MutableList<String>,
                 itemList: MutableList<ItemStack>) {
        if (holoEntityIDMap.containsKey(id)) {
            // Msg, id不存在消息
            return
        }

        val holoIds : MutableList<Int> = ArrayList()
        holoEntityIDMap[id] = holoIds

        for ((index) in contents.withIndex()) {
            val entityID = randomIntID.nextInt()

            loc.add(0.0, -0.22, 0.0)

            spawnAS(player, entityID, loc)

            // 处理动画文字
            updateWrite(player, entityID, contents[index])

            if (itemList.size > index) {setItem(player, entityID, itemList[index])}

            val ids = holoEntityIDMap[id]!!
            ids.add(entityID)
            holoEntityIDMap[id] = ids
        }
    }*/

    /*@TSchedule
    fun updateWrite(player: Player, entityID: Int, text: String) {
        object : BukkitRunnable() {
            override fun run() {
                if (!player.isOnline || player.isDead) {
                    cancel()
                    return
                }
                *//*
                等待处理动画文字
                 *//*
                val newText = text
                setText(player, entityID, newText)
            }
        }.runTaskTimer(QuestEngine.plugin, 0L, 20L)
    }*/

    // 防止EntityID相似而冲突
    companion object {
        @JvmStatic
        private val randomIntEntityID = Random()

        @JvmStatic
        private var holoEntityIDMap = mutableMapOf<String, MutableList<Int>>()
    }

    fun isHoloPacket(entityID: Int): Boolean {
        holoEntityIDMap.forEach {
            if (it.value[0] == entityID) return true
        }
        return false
    }
}