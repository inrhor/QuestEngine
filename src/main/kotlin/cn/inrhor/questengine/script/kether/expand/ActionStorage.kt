package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.common.database.data.delStorage
import cn.inrhor.questengine.common.database.data.getStorageValue
import cn.inrhor.questengine.common.database.data.setStorage
import cn.inrhor.questengine.script.kether.player
import taboolib.library.kether.ArgTypes
import taboolib.module.kether.*

class ActionStorage {

    companion object {
        internal object Parser {
            @KetherParser(["storage"], shared = true)
            fun parser() = scriptParser {
                val a = it.next(ArgTypes.ACTION)
                it.switch {
                    case("get") {
                        actionNow {
                            newFrame(a).run<String>().thenAccept { e ->
                                player().getStorageValue(e)
                            }
                        }
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