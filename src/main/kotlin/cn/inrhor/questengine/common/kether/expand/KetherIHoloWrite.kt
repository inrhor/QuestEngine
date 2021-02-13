package cn.inrhor.questengine.common.kether.expand

import cn.inrhor.questengine.common.dialog.location.FixedLocation
import io.izzel.taboolib.kotlin.kether.KetherError
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.common.util.Coerce
import java.util.concurrent.CompletableFuture

class KetherIHoloWrite(
) : QuestAction<FixedLocation>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<FixedLocation>? {

    }

    companion object {
        @KetherParser(["fixedLocation"], namespace = "QuestEngine")
        fun parser() = ScriptParser.parser {

        }
    }
}