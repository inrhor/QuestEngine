package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.common.dialog.animation.item.ItemDialogPlay
import cn.inrhor.questengine.common.dialog.animation.text.type.TextWrite
import cn.inrhor.questengine.common.item.ItemManager
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class KetherContextWrite {

    class WriteItem(val itemID: String, val delay: Int) : ScriptAction<ItemDialogPlay>() {
        override fun run(frame: ScriptFrame): CompletableFuture<ItemDialogPlay> {
            val dialogItem = CompletableFuture<ItemDialogPlay>()
            dialogItem.complete(
                ItemDialogPlay(
                    ItemManager.get(itemID),
                    delay
                )
            )
            return dialogItem
        }
    }

    class WriteText(val text: String, val delay: Int, val speedWrite: Int) : ScriptAction<TextWrite>() {
        override fun run(frame: ScriptFrame): CompletableFuture<TextWrite> {
            val fixedLocation = CompletableFuture<TextWrite>()
            fixedLocation.complete(
                TextWrite(
                    delay,
                    speedWrite,
                    text
                )
            )
            return fixedLocation
        }
    }

    enum class Type {
        ITEM, TEXT
    }

    /*
        contextWrite delay [...] to [item/text] use [normal/typeWrite] context [...] <text: speed [...]>
     */
    internal object Parser {
        @KetherParser(["contextWrite"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            it.mark()
            it.expect("delay")
            val delay = it.nextInt()
            it.mark()
            it.expect("to")
            val contextType = try {
                when (val type = it.nextToken()) {
                    "item" -> Type.ITEM
                    "text" -> Type.TEXT
                    else -> throw KetherError.CUSTOM.create("未知内容类型: $type")
                }
            } catch (ignored: Exception) {
                it.reset()
                Type.TEXT
            }
            it.mark()
            it.expect("context")
            val context = it.nextToken()
            when (contextType) {
                Type.ITEM -> WriteItem(context, delay)
                Type.TEXT -> WriteText(context, delay, it.run {
                    it.mark()
                    it.expect("speed")
                    it.nextInt()
                })
            }
        }
    }
}