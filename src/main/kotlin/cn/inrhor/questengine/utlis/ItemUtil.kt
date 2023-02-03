package cn.inrhor.questengine.utlis

import org.bukkit.inventory.ItemStack

object ItemUtil {

    fun MutableList<ItemStack>.getAmount(): Int {
        var a = 0
        forEach {
            a += it.amount
        }
        return a
    }

    fun MutableList<ItemStack>.take(amount: Int) {
        var a = 0
        forEach {
            if (a < amount) {
                for (i in 0 until  it.amount) {
                    if (a >= amount) break
                    it.amount--
                    a++
                }
            }
        }
    }

}