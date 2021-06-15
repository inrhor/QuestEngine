package cn.inrhor.questengine.common.kether.expand

import cn.inrhor.questengine.common.dialog.animation.item.ItemDialogPlay
import cn.inrhor.questengine.common.item.ItemManager
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import java.util.concurrent.CompletableFuture

class KetherIItemNormal(
    val itemID: String,
    val delay: Int
) : QuestAction<ItemDialogPlay>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<ItemDialogPlay>? {
        val dialogItem = CompletableFuture<ItemDialogPlay>()
        dialogItem.complete(
            ItemDialogPlay(
                ItemManager().get(itemID),
                delay
            )
        )
        return dialogItem
    }

    companion object {
        fun parser() = ScriptParser.parser {
            val delay = it.nextInt()
            val itemID = it.nextToken()
            KetherIItemNormal(itemID, delay)
        }
    }
}