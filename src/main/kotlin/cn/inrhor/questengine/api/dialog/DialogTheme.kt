package cn.inrhor.questengine.api.dialog

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * 对话主题接口
 */
interface  DialogTheme {

    /**
     * 播放对话
     */
    fun play()

    /**
     * 终止对话
     */
    fun end()

    /**
     * 添加 viewer
     */
    fun addViewer(viewer: Player)

    /**
     * 移除 viewer
     */
    fun deleteViewer(viewer: Player)

}

/**
 * 对话播放抽象
 */
abstract class DialogPlay {
    var delay: Long = 0
    var speed: Long = 0
}

/**
 * 对话文本抽象
 */
abstract class TextPlay: DialogPlay() {
    var text: String = ""
}

/**
 * 对话物品抽象
 */
abstract class ItemPlay: DialogPlay() {
    var itemStack: ItemStack = ItemStack(Material.STONE)
    var displayType: Type = Type.FIXED

    enum class Type {
        FIXED, SUSPEND
    }
}