package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.common.item.ItemManager
import cn.inrhor.questengine.script.kether.player
import cn.inrhor.questengine.utlis.bukkit.InvSlot
import cn.inrhor.questengine.utlis.bukkit.ItemMatch
import org.bukkit.inventory.ItemStack
import taboolib.common5.Coerce
import taboolib.common5.Demand
import taboolib.module.kether.*
import taboolib.library.kether.*
import java.util.concurrent.CompletableFuture

class KetherItem {

    //itemCheck inv all "MINECRAFT -material SLIME_BALL -customModelData 2215 -amount 5"
    class CheckInv(val type: String, val item: ParsedAction<*>): ScriptAction<Boolean>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            return frame.newFrame(item).run<Any>().thenApply {
                val player = frame.player()
                ItemMatch(Demand(it.toString())).slotHas(player, InvSlot.valueOf(type.uppercase()))
            }
        }
    }

    //itemCheck take "MINECRAFT -material SLIME_BALL -customModelData 2215 -amount 5"
    class TakeInv(val item: ParsedAction<*>): ScriptAction<Boolean>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            return frame.newFrame(item).run<Any>().thenApply {
                val player = frame.player()
                ItemMatch(Demand(it.toString())).slotHas(player, take = true)
            }
        }
    }

    internal object Parser {
        @KetherParser(["itemCheck"])
        fun parser() = scriptParser {
            it.mark()
            when (it.expects("inv", "take")) {
                "inv" -> CheckInv(it.nextToken(), it.next(ArgTypes.ACTION))
                "take" -> TakeInv(it.next(ArgTypes.ACTION))
                else -> error("unknown type")
            }
        }

        @KetherParser(["itemBuild"], shared = true)
        fun parserItem() = scriptParser {
            it.switch {
                case("id") {
                    val itemID = it.next(ArgTypes.ACTION)
                    it.expect("data")
                    val data = it.next(ArgTypes.ACTION)
                    it.expect("note")
                    val note = it.next(ArgTypes.ACTION)
                    it.expect("with")
                    val k = it.next(ArgTypes.ACTION)
                    HookItemAction(itemID, data, note, k)
                }
            }
        }

    }

    class HookItemAction(val itemID: ParsedAction<*>, val data: ParsedAction<*>, val note: ParsedAction<*>, val k:
    ParsedAction<*>):
        ScriptAction<ItemStack>() {
        @Suppress("UNCHECKED_CAST")
        override fun run(frame: ScriptFrame): CompletableFuture<ItemStack> {
            val future = CompletableFuture<ItemStack>()
            frame.newFrame(itemID).run<String>().thenAccept {
                frame.newFrame(data).run<Any>().thenAccept { d ->
                    val dl = (Coerce.toList(d)?: listOf()) as List<String>
                    frame.newFrame(note).run<Any>().thenAccept { n ->
                        val nl = (Coerce.toList(n)?: listOf()) as List<String>
                        frame.newFrame(k).run<String>().thenAccept { key ->
                            future.complete(ItemManager.itemHook(frame.player(), it, dl, nl, key) { s ->
                                frame.variables().values().forEach { a ->
                                    s.rootFrame().variables().set(a.key, a.value)
                                }
                            })
                        }
                    }
                }
            }
            return future
        }
    }

}