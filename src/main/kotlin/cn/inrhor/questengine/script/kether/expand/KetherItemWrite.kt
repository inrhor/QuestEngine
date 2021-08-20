package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.common.dialog.animation.item.ItemDialogPlay
import cn.inrhor.questengine.common.item.ItemManager
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class KetherItemWrite {

    class WriteItem(val itemID: String, val delay: Int) : ScriptAction<ItemDialogPlay>() {
        override fun run(frame: ScriptFrame): CompletableFuture<ItemDialogPlay> {
            val dialogItem = CompletableFuture<ItemDialogPlay>()
            val item = ItemManager.get(itemID)
            dialogItem.complete(
                ItemDialogPlay(
                    item,
                    delay
                )
            )
            return dialogItem
        }
    }

    /*
        itemWrite [delay] use [normal] item [...]
     */
    internal object Parser {
        @KetherParser(["itemWrite"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            val delay = it.nextInt()
            it.mark()
            it.expect("use")
            it.mark()
            it.expect("normal")
            it.mark()
            it.expect("item")
            val itemID = it.nextToken()
            WriteItem(itemID, delay)
        }
    }
}