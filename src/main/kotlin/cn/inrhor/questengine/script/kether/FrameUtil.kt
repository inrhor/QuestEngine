package cn.inrhor.questengine.script.kether

import org.bukkit.entity.Player
import taboolib.library.kether.QuestReader
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.script
import java.util.*
import java.util.concurrent.CompletableFuture

fun frameVoid(): CompletableFuture<Void> = CompletableFuture.completedFuture(null)

fun ScriptFrame.selectQuestID() = variables().get<Any?>(ActionSelect.ID.variable[0])
    .orElse(null)?.toString()?: selectQuestUid().toString()

fun ScriptFrame.selectQuestUid() = UUID.fromString(variables().get<Any?>(ActionSelect.UUID.variable[0])
    .orElse(null)?.toString())?: error("unknown quest uuid or id")

fun ScriptFrame.selectInnerID() = variables().get<Any?>(ActionSelect.ID.variable[1])
    .orElse(null)?.toString()?: error("unknown inner id")

fun ScriptFrame.selectTargetID() = variables().get<Any?>(ActionSelect.ID.variable[2])
    .orElse(null)?.toString()?: error("unknown target id")

fun ScriptFrame.selectNavID() = variables().get<Any?>(ActionSelect.ID.variable[4])
    .orElse(null)?.toString()?: error("unknown nav id")

fun ScriptFrame.selectDialogID() = variables().get<Any?>(ActionSelect.ID.variable[5])
    .orElse(null)?.toString()?: error("unknown dialog id")

fun ScriptFrame.selectControlID() = variables().get<Any?>(ActionSelect.ID.variable[6])
    .orElse(null)?.toString()?: error("unknown control id")

fun ScriptFrame.player() = script().sender?.castSafely<Player>()?: error("unknown player")

enum class ActionSelect(vararg val variable: String) {
    ID("@QenQuestId", "@QenInnerId", "@QenTargetId", "@QenXxxx", "@QenNavID",
        "@QenDialogID", "@QenControlID"),
    UUID("@QenQuestUid")
}

fun QuestReader.selectType() = try {
    mark()
    expect("useUid")
    ActionSelect.UUID
}catch (ex: Exception) {
    reset()
    ActionSelect.ID
}