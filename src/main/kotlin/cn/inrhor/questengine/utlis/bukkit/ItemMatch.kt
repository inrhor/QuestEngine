package cn.inrhor.questengine.utlis.bukkit

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common5.Demand
import taboolib.platform.util.isNotAir

class ItemMatch(val itemType: ItemType = ItemType.MINECRAFT,
                val displayName: String?,
                val loreContains: String?,
                val customModelData: Int?,
                val amount: Int?) {

    constructor(d: Demand): this(ItemType.valueOf(d.namespace),
        d.get("displayName"),
        d.get("loreContains"),
        d.get("customModelData")?.toInt(),
        d.get("amount")?.toInt())

    fun check(itemStack: ItemStack, take: Boolean = false): Boolean {
        /*when (itemType) {
            ItemType.MINECRAFT -> {

            }
        }*/
        val meta = itemStack.itemMeta
        if (displayName != null && displayName != meta?.displayName) return false
        if (loreContains != null && meta?.lore?.contains(loreContains) == false) return false
        if (customModelData != null) {
            if (meta?.customModelData != customModelData) return false
        }
        if (amount != null && itemStack.amount < amount) return false
        itemStack.amount -= 1
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
    MINECRAFT
}

enum class InvSlot {
    ALL, MANDHAND
}