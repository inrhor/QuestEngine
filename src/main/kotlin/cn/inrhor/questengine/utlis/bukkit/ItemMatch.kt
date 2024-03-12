package cn.inrhor.questengine.utlis.bukkit

import cn.inrhor.questengine.utlis.ItemUtil.getAmount
import cn.inrhor.questengine.utlis.ItemUtil.take
import dev.lone.itemsadder.api.CustomStack
import io.lumine.mythic.lib.api.item.NBTItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.common5.Demand
import taboolib.platform.util.checkItem

class ItemMatch(val itemType: ItemType = ItemType.MINECRAFT,
                val material: Material?,
                val displayName: String?,
                val loreContains: String?,
                val customModelData: Int?,
                val itemId: String?,
                val amount: Int = 1) {

    constructor(d: Demand): this(ItemType.valueOf(d.namespace),
        d.get("material")?.uppercase()?.let { Material.valueOf(it) },
        d.get("displayName"),
        d.get("loreContains"),
        d.get("customModelData")?.toInt(),
        d.get("id"),
        d.get("amount")?.toInt()?: 1)

    private fun check(itemStack: ItemStack): ItemStack? {
        if (itemType == ItemType.MINECRAFT) {
            if (material != null && itemStack.type != material) return null
            val meta = itemStack.itemMeta
            if (displayName != null) {
                if (meta?.displayName == null) {
                    return null
                } else {
                    if (meta.displayName != displayName) {
                        return null
                    }
                }
            }
            if (loreContains != null) {
                if (meta?.lore == null) {
                    return null
                } else {
                    if (!meta.lore!!.contains(loreContains)) {
                        return null
                    }
                }
            }
            if (customModelData != null) {
                if (meta == null) {
                    return null
                } else {
                    if (!meta.hasCustomModelData()) {
                        return null
                    } else {
                        if (meta.customModelData != customModelData) return null
                    }
                }
            }
        }else if (itemType == ItemType.MMOITEMS) {
            // 判断itemStack是否为根据mmoitems id mmoitem 某一个物品
            if (NBTItem.get(itemStack).getString("MMOITEMS_ITEM_ID") != itemId) {
                return null
            }
        }else if (itemType == ItemType.ITEMSADDER) {
            val i = CustomStack.byItemStack(itemStack)
            if (i?.id != itemId) {
                return null
            }
        }
        return itemStack
    }

    fun checkItem(itemStack: ItemStack, inventory: Inventory, take: Boolean = false): Boolean {
        return inventory.checkItem(itemStack, amount, take)
    }

    fun checkPlayerItem(player: Player, inventory: Inventory, invSlot: InvSlot = InvSlot.ALL, take: Boolean = false): Boolean {
        if (invSlot == InvSlot.ALL) {
            if (itemType != ItemType.MINECRAFT) {
                inventory.forEach {
                    if (it != null) {
                        val itemStack = check(it)
                        if (itemStack != null) {
                            if (inventory.checkItem(itemStack, amount, take)) return true
                        }
                    }
                }
            }else {
                val items = mutableListOf<ItemStack>()
                inventory.forEach {
                    val am = items.getAmount()
                    if (am >= amount) {
                        if (take) {
                            items.take(amount)
                        }
                        return true
                    }else {
                        if (it != null) {
                            val itemStack = check(it)
                            if (itemStack != null) {
                                items.add(itemStack)
                            }
                        }
                    }
                }
                val am = items.getAmount()
                if (am >= amount) {
                    if (take) {
                        items.take(amount)
                    }
                    return true
                }
                return false
            }
        }else {
            val itemStack = player.equipment?.itemInMainHand?: return false
            if (itemStack.amount >= amount) {
                if (take) itemStack.amount -= amount
                return true
            }
        }
        return false
    }

    fun slotHas(player: Player, invSlot: InvSlot = InvSlot.ALL, take: Boolean = false): Boolean {
        return checkPlayerItem(player, player.inventory, invSlot, take)
    }

}

enum class ItemType {
    MINECRAFT, MMOITEMS, ITEMSADDER
}

enum class InvSlot {
    ALL, MAINHAND
}