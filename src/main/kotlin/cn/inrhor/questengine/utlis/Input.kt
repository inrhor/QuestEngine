package cn.inrhor.questengine.utlis

import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEditBookEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit
import taboolib.library.xseries.XMaterial
import taboolib.module.lang.asLangText
import taboolib.platform.util.buildBook
import taboolib.platform.util.giveItem
import taboolib.platform.util.hasLore
import taboolib.platform.util.takeItem
import java.util.concurrent.ConcurrentHashMap

object Input {

    fun Player.inputBook(display: String, disposable: Boolean = true, content: List<String> = emptyList(), catcher: (List<String>) -> Unit) {
        // 移除正在编辑的书本
        inventory.takeItem(99) { i -> i.hasLore(BookEditListener.regex[0]) }
        // 发送书本
        giveItem(
            buildBook {
                write(content.joinToString("\n"))
                setMaterial(XMaterial.WRITABLE_BOOK)
                name = "§f$display"
                lore += BookEditListener.regex[0]
                lore += if (disposable) {
                    BookEditListener.regex[1]
                } else {
                    BookEditListener.regex[2]
                }
            }
        )
        BookEditListener.inputs[name] = catcher
    }

    internal object BookEditListener {

        internal val regex = arrayOf(
            console().asLangText("EDIT_BOOK_OPEN"),
            console().asLangText("EDIT_BOOK_DELETE"),
            console().asLangText("EDIT_BOOK_KEEP"))

        internal val inputs = ConcurrentHashMap<String, (List<String>) -> Unit>()

        @SubscribeEvent
        fun onPlayerEditBookEvent(event: PlayerEditBookEvent) {
            val lore = event.newBookMeta.lore
            if (lore != null && lore.getOrNull(0) == regex[0]) {
                val consumer = inputs[event.player.name] ?: return
                val pages = event.newBookMeta.pages.flatMap { TextComponent(it).toPlainText().replace("§0", "").split("\n") }
                consumer(pages)
                if (lore.getOrNull(1) == regex[1]) {
                    inputs.remove(event.player.name)
                    submit(delay = 1) {
                        event.player.inventory.takeItem(99) { i -> i.hasLore(regex[0]) }
                    }
                }
            }
        }
    }

}