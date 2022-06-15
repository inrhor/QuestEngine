package cn.inrhor.questengine.script.kether

import org.bukkit.entity.Player
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.script
import java.util.concurrent.CompletableFuture

fun frameVoid(): CompletableFuture<Void> = CompletableFuture.completedFuture(null)

fun ScriptFrame.selectQuestID() = variables().get<Any?>("@QenQuestID")
    .orElse(null)?.toString()?: error("unknown quest uuid or id")

fun ScriptFrame.selectTargetID() = variables().get<Any?>("@QenTargetID")
    .orElse(null)?.toString()?: error("unknown target id")

fun ScriptFrame.selectNavID() = variables().get<Any?>("@QenNavID")
    .orElse(null)?.toString()?: error("unknown nav id")

fun ScriptFrame.selectDialogID() = variables().get<Any?>("@QenDialogID")
    .orElse(null)?.toString()?: error("unknown dialog id")

fun ScriptFrame.player() = script().sender?.castSafely<Player>()?: error("unknown player")