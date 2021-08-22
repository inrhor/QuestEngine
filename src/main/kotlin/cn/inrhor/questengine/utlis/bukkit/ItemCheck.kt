package cn.inrhor.questengine.utlis.bukkit

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.platform.util.isAir
import taboolib.platform.util.isNotAir

class ItemCheck(private val matchers: MutableMap<String, String>) {

    companion object {
        fun eval(str: String): ItemCheck {
            return split(str)
        }

        private fun split(str: String): ItemCheck {
            val map = mutableMapOf<String, String>()
            val list = str.split(";")
            list.forEach {
                val sp = it.split(":")
                map[sp[0]] = sp[1]
            }
            return ItemCheck(map)
        }
    }

    fun match(itemStack: ItemStack, take: Boolean): Boolean {
        val meta = itemStack.itemMeta
        var amount = 0
        matchers.forEach { (mark, data) ->
            when (mark.lowercase()) {
                "minecraft" -> if (data.uppercase() != itemStack.type.name) return false
                "displayname" -> if (meta == null || data != meta.displayName) return false
                "lorecontains" -> if (meta == null || !loreContains(meta, data)) return false
                "custonmodeldata" -> if (meta == null || meta.customModelData != data.toInt()) return false
                "amount" -> if (itemStack.amount < data.toInt()) return false else if (take) amount = data.toInt()
            }
        }
        itemStack.amount = itemStack.amount-amount
        return true
    }

    fun invHas(player: Player, take: Boolean): Boolean {
        if (player.inventory.isEmpty) return false
        player.inventory.forEach {
            if (it.isNotAir()) {
                if (match(it, take)) return true
            }
        }
        return false
    }

    fun isMainHand(player: Player, take: Boolean): Boolean {
        if (player.inventory.itemInMainHand.isAir()) return false
        return match(player.inventory.itemInMainHand, take)
    }

    private fun loreContains(meta: ItemMeta, str: String): Boolean {
        val lore = meta.lore?: return false
        return lore.contains(str)
    }

}