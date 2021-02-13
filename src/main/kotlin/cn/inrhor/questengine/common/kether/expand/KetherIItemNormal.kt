package cn.inrhor.questengine.common.kether.expand

import cn.inrhor.questengine.common.dialog.animation.item.DialogItem
import cn.inrhor.questengine.common.item.ItemManager
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import java.util.concurrent.CompletableFuture

class KetherIItemNormal(
    val itemID: String,
    val delay: Int
) : QuestAction<DialogItem>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<DialogItem>? {
        val fixedLocation = CompletableFuture<DialogItem>()
        fixedLocation.complete(
            DialogItem(
                ItemManager().get(itemID)!!.item!!,
                delay
            )
        )
        return fixedLocation
    }

    companion object {
        fun parser() = ScriptParser.parser {
            val dialogID = it.nextToken()
            val delay = it.nextInt()
            KetherIItemNormal(dialogID, delay)
        }
    }
}