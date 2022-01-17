package cn.inrhor.questengine.api.packet

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.nms.NMS
import cn.inrhor.questengine.common.hook.HookProtocolLib
import cn.inrhor.questengine.utlis.UtilString
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang

object PacketUtil {

    var hookPacket = HookPacket.DEFAULT

    @Awake(LifeCycle.ACTIVE)
    fun setHook() {
        val hook = QuestEngine.config.getString("hook.packet")?:"TabooLib".uppercase()
        if (hook == "PROTOCOLLIB") {
            if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
                console().sendLang("LOADER-NOT_INSTALLED-PLIB", UtilString.pluginTag)
                return
            }
            hookPacket = HookPacket.PROTOCOLLIB
        }
    }
}

fun spawnEntity(players: MutableSet<Player>, entityId: Int, entityType: String, location: Location) {
    when (PacketUtil.hookPacket) {
        HookPacket.DEFAULT -> getPackets().spawnEntity(players, entityId, entityType, location)
        HookPacket.PROTOCOLLIB -> HookProtocolLib.spawnEntity(players, entityId, entityType, location)
    }
}

fun spawnAS(players: MutableSet<Player>, entityId: Int, location: Location) {
    when (PacketUtil.hookPacket) {
        HookPacket.DEFAULT -> getPackets().spawnAS(players, entityId, location)
        HookPacket.PROTOCOLLIB -> HookProtocolLib.spawnAS(players, entityId, location)
    }
}

fun initAS(player: Player, entityId: Int, showName: Boolean, isSmall: Boolean, marker: Boolean) {
    initAS(mutableSetOf(player), entityId, showName, isSmall, marker)
}

fun initAS(players: MutableSet<Player>, entityId: Int, showName: Boolean, isSmall: Boolean, marker: Boolean) {
    when (PacketUtil.hookPacket) {
        HookPacket.DEFAULT -> getPackets().initAS(players, entityId, showName, isSmall, marker)
        HookPacket.PROTOCOLLIB -> HookProtocolLib.initAS(players, entityId, showName, isSmall, marker)
    }
}

fun spawnItem(players: MutableSet<Player>, entityId: Int, location: Location, itemStack: ItemStack) {
    when (PacketUtil.hookPacket) {
        HookPacket.DEFAULT -> getPackets().spawnItem(players, entityId, location, itemStack)
        HookPacket.PROTOCOLLIB -> HookProtocolLib.spawnItem(players, entityId, location, itemStack)
    }
}

fun destroyEntity(player: Player, entityId: Int) {
    when (PacketUtil.hookPacket) {
        HookPacket.DEFAULT -> getPackets().destroyEntity(player, entityId)
        HookPacket.PROTOCOLLIB -> HookProtocolLib.destroyEntity(player, entityId)
    }
}

fun destroyEntity(players: MutableSet<Player>, entityId: Int) {
    when (PacketUtil.hookPacket) {
        HookPacket.DEFAULT -> getPackets().destroyEntity(players, entityId)
        HookPacket.PROTOCOLLIB -> HookProtocolLib.destroyEntity(players, entityId)
    }
}

fun updateEquipmentItem(players: MutableSet<Player>, entityId: Int, slot: EquipmentSlot, itemStack: ItemStack) {
    when (PacketUtil.hookPacket) {
        HookPacket.DEFAULT -> getPackets().updateEquipmentItem(players, entityId, slot, itemStack)
        HookPacket.PROTOCOLLIB -> HookProtocolLib.updateEquipmentItem(players, entityId, slot, itemStack)
    }
}

fun updatePassengers(players: MutableSet<Player>, entityId: Int, passengers: Int) {
    when (PacketUtil.hookPacket) {
        HookPacket.DEFAULT -> getPackets().updatePassengers(players, entityId, passengers)
        HookPacket.PROTOCOLLIB -> HookProtocolLib.updatePassengers(players, entityId, passengers)
    }
}

fun updateDisplayName(players: MutableSet<Player>, entityId: Int, name: String) {
    when (PacketUtil.hookPacket) {
        HookPacket.DEFAULT -> getPackets().updateDisplayName(players, entityId, name)
        HookPacket.PROTOCOLLIB -> HookProtocolLib.updateDisplayName(players, entityId, name)
    }
}

fun updateDisplayName(player: Player, entityId: Int, name: String) {
    when (PacketUtil.hookPacket) {
        HookPacket.DEFAULT -> getPackets().updateDisplayName(player, entityId, name)
        HookPacket.PROTOCOLLIB -> HookProtocolLib.updateDisplayName(player, entityId, name)
    }
}

fun updateLocation(players: MutableSet<Player>, entityId: Int, location: Location) {
    when (PacketUtil.hookPacket) {
        HookPacket.DEFAULT -> getPackets().updateLocation(players, entityId, location)
        HookPacket.PROTOCOLLIB -> HookProtocolLib.updateLocation(players, entityId, location)
    }
}

fun setEntityCustomNameVisible(players: MutableSet<Player>, entityId: Int, visible: Boolean) {
    when (PacketUtil.hookPacket) {
        HookPacket.DEFAULT -> getPackets().updateEntityMetadata(players, entityId,
            getPackets().getMetaEntityCustomNameVisible(visible))
        HookPacket.PROTOCOLLIB -> HookProtocolLib.setEntityCustomNameVisible(players, entityId, visible)
    }
}

fun isInvisible(players: MutableSet<Player>, entityId: Int) {
    when (PacketUtil.hookPacket) {
        HookPacket.DEFAULT -> getPackets().updateEntityMetadata(players, entityId, getPackets().getIsInvisible())
        HookPacket.PROTOCOLLIB -> HookProtocolLib.isInvisible(players, entityId)
    }
}

/**
 * 观察者实体视角
 */
fun camera(player: Player, entityId: Int) {
    when (PacketUtil.hookPacket) {
        HookPacket.DEFAULT -> getPackets().camera(player, entityId)
        HookPacket.PROTOCOLLIB -> HookProtocolLib.camera(player, entityId)
    }
}

fun getPackets(): NMS {
    return NMS.INSTANCE
}

enum class HookPacket {
    DEFAULT, PROTOCOLLIB
}