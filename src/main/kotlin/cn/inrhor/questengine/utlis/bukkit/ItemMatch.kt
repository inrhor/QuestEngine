package cn.inrhor.questengine.utlis.bukkit

import io.lumine.mythic.lib.api.item.NBTItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common5.Demand
import taboolib.platform.util.isNotAir

class ItemMatch(val itemType: ItemType = ItemType.MINECRAFT,
                val material: Material?,
                val displayName: String?,
                val loreContains: String?,
                val customModelData: Int?,
                val mmoItemID: String?,
                val amount: Int?) {

    constructor(d: Demand): this(ItemType.valueOf(d.namespace),
        d.get("material")?.uppercase()?.let { Material.valueOf(it) },
        d.get("displayName"),
        d.get("loreContains"),
        d.get("customModelData")?.toInt(),
        d.get("id"),
        d.get("amount")?.toInt())

    fun check(itemStack: ItemStack, take: Boolean = false): Boolean {
        if (amount != null && itemStack.amount < amount) return false
        if (itemType == ItemType.MINECRAFT) {
            if (material != null && itemStack.type != material) return false
            val meta = itemStack.itemMeta
            if (displayName != null) {
                if (meta?.displayName == null) {
                    return false
                } else {
                    if (meta.displayName != displayName) {
                        return false
                    }
                }
            }
            if (loreContains != null) {
                if (meta?.lore == null) {
                    return false
                } else {
                    if (!meta.lore!!.contains(loreContains)) {
                        return false
                    }
                }
            }
            if (customModelData != null) {
                if (meta == null) {
                    return false
                } else {
                    if (!meta.hasCustomModelData()) {
                        return false
                    } else {
                        if (meta.customModelData != customModelData) return false
                    }
                }
            }
        }else if (itemType == ItemType.MMOITEMS) {
            // 判断itemStack是否为根据mmoitems id mmoitem 某一个物品
            if (NBTItem.get(itemStack).getString("MMOITEMS_ITEM_ID") != mmoItemID) {
                return false
            }
        }
        if (take) itemStack.amount -= 1
        return true
    }

    fun slotHas(player: Player, invSlot: InvSlot = InvSlot.ALL, take: Boolean = false): Boolean {
        when (invSlot) {
            InvSlot.MANDHAND -> return check(player.inventory.itemInMainHand, take)
            else -> {
                player.inventory.forEach {
                    if (it.isNotAir()) {
                        if (check(it, take)) return true
                    }
                }
            }
        }
        return false
    }

}

enum class ItemType {
    MINECRAFT, MMOITEMS
}

enum class InvSlot {
    ALL, MANDHAND
}