package cn.inrhor.questengine.common.hologram

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class IHolo(
    val holoID: String,
    val location: Location
) {

    val viewers: MutableList<Player> = mutableListOf()

    val textList: MutableList<String> = mutableListOf()
    val itemList: MutableList<ItemStack> = mutableListOf()

    val text: String = ""
    val item: ItemStack = ItemStack(Material.AIR)

    /**
     * 刷新全息的内容并发送
     */
    fun update() {

    }

    /**
     * 添加可视者并发送
     */
    fun addViewer(player: Player) {

    }

    /**
     * 删除可视者并销毁他的全息
     */
    fun removeViewer(player: Player) {

    }

    /*var follow : Boolean? = false
    var distance : Double? = 0.0

    fun move() {

        *//*viewers?.forEach {
            THologram.create(location, contentList?.get(0), it)
        }*//*
    }*/

}