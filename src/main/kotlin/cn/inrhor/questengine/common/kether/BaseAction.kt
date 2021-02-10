package cn.inrhor.questengine.common.kether

import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/*
 * 源码来自 TrMenu by Arasple
 */
abstract class BaseAction<T> : QuestAction<T>() {

    internal val completableFuture: CompletableFuture<Void> = CompletableFuture.completedFuture(null)

    /**
     * 执行者类型是否为玩家
     */
    fun QuestContext.Frame.viewer(): Player {
        return ((this.context() as ScriptContext).sender ?:
        throw RuntimeException(TLocale.asString(""))) as Player
    }

}