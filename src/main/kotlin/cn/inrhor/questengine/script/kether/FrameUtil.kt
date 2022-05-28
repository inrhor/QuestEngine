package cn.inrhor.questengine.script.kether

import org.bukkit.entity.Player
import taboolib.library.kether.QuestReader
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.script
import java.util.*
import java.util.concurrent.CompletableFuture

fun frameVoid(): CompletableFuture<Void> = CompletableFuture.completedFuture(null)

fun ScriptFrame.selectQuestID() = variables().get<Any?>("@QenQuestId")
    .orElse(null)?.toString()?: error("unknown quest id")

fun ScriptFrame.selectQuestUid() = UUID.fromString(variables().get<Any?>("@QenQuestUid")
    .orElse(null)?.toString())?: error("unknown quest uuid")

fun ScriptFrame.player() = script().sender?.castSafely<Player>()?: error("unknown player")

enum class ActionSelect(vararg val variable: String) {
    ID("@QenQuestId"),
    UUID("@QenQuestUid")
}

fun QuestReader.selectType() = try {
    mark()
    expect("useUid")
    ActionSelect.ID
}catch (ex: Exception) {
    reset()
    ActionSelect.UUID
}