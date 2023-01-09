package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.api.manager.DataManager.delStorage
import cn.inrhor.questengine.api.manager.DataManager.getStorageValue
import cn.inrhor.questengine.api.manager.DataManager.setStorage
import cn.inrhor.questengine.script.kether.player
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionStorage {

    class valueStr(val s: ParsedAction<*>): ScriptAction<String>() {
        override fun run(frame: ScriptFrame): CompletableFuture<String> {
            return frame.newFrame(s).run<String>().thenApply { e ->
                frame.player().getStorageValue(e)
            }
        }
    }

    companion object {
        internal object Parser {
            @KetherParser(["storage"], shared = true)
            fun parser() = scriptParser {
                val a = it.next(ArgTypes.ACTION)
                it.switch {
                    case("get") {
                        valueStr(a)
                    }
                    case("set") {
                        val b = it.next(ArgTypes.ACTION)
                        actionNow {
                            newFrame(a).run<String>().thenAccept { e ->
                                newFrame(b).run<String>().thenAccept { f ->
                                    player().setStorage(e, f)
                                }
                            }
                        }
                    }
                    case("remove") {
                        actionNow {
                            newFrame(a).run<String>().thenAccept { e ->
                                player().delStorage(e)
                            }
                        }
                    }
                }
            }
        }
    }

}