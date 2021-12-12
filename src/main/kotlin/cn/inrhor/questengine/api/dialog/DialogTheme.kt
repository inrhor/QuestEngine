package cn.inrhor.questengine.api.dialog

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * 对话主题接口
 */
interface  DialogTheme {

    /**
     * 播放对话
     */
    fun play()

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
    var itemID: String = ""
    var displayType: Type = Type.FIXED

    enum class Type {
        FIXED, SUSPEND
    }
}