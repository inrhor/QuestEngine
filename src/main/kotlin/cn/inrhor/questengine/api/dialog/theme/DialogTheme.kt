package cn.inrhor.questengine.api.dialog.theme

import cn.inrhor.questengine.api.dialog.DialogModule
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * 对话主题抽象
 */
abstract class DialogTheme(var type: Type = Type.Chat) {

    enum class Type {
        Holo, Chat
    }

    abstract val dialogModule: DialogModule

    abstract val viewers: MutableSet<Player>

    abstract val npcLoc: Location

    /**
     * 播放对话
     */
    abstract fun play()

    /**
     * 终止对话
     */
    abstract fun end()

    /**
     * 添加 viewer
     */
    abstract fun addViewer(viewer: Player)

    /**
     * 移除 viewer
     */
    abstract fun deleteViewer(viewer: Player)

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