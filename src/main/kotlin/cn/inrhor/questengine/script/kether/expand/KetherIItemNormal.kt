package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.common.dialog.animation.item.ItemDialogPlay
import cn.inrhor.questengine.common.item.ItemManager
import taboolib.library.kether.*
import taboolib.module.kether.KetherParser
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class KetherIItemNormal(val itemID: String, val delay: Int) : QuestAction<ItemDialogPlay>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<ItemDialogPlay>? {
        val dialogItem = CompletableFuture<ItemDialogPlay>()
        dialogItem.complete(
            ItemDialogPlay(
                ItemManager.get(itemID),
                delay
            )
        )
        return dialogItem
    }

    companion object {
        @KetherParser(["itemNormal"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            val delay = it.nextInt()
            val itemID = it.nextToken()
            KetherIItemNormal(itemID, delay)
        }
    }
}