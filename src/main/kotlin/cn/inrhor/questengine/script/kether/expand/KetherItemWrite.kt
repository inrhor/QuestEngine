package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.common.dialog.animation.item.ItemDialogPlay
import cn.inrhor.questengine.common.item.ItemManager
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class KetherItemWrite {

    class WriteItem(val itemID: String, val type: ItemDialogPlay.Type, val delay: Int) : ScriptAction<ItemDialogPlay>() {
        override fun run(frame: ScriptFrame): CompletableFuture<ItemDialogPlay> {
            val dialogItem = CompletableFuture<ItemDialogPlay>()
            val item = ItemManager.get(itemID)
            dialogItem.complete(
                ItemDialogPlay(
                    item,
                    type,
                    delay
                )
            )
            return dialogItem
        }
    }

    /*
        itemWrite [delay] use [suspend/fixed] item [...]
     */
    internal object Parser {
        @KetherParser(["itemWrite"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            val delay = it.nextInt()
            it.mark()
            it.expect("use")
            val itemType = try {
                when (it.nextToken()) {
                    "suspend" -> ItemDialogPlay.Type.SUSPEND
                    else -> ItemDialogPlay.Type.FIXED
                }
            } catch (ignored: Exception) {
                ItemDialogPlay.Type.FIXED
            }
            it.mark()
            it.expect("item")
            val itemID = it.nextToken()
            WriteItem(itemID, itemType, delay)
        }
    }
}