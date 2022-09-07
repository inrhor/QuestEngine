package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.common.dialog.DialogManager
import cn.inrhor.questengine.script.kether.player
import cn.inrhor.questengine.script.kether.selectDialogID
import org.bukkit.Location
import taboolib.library.kether.ArgTypes
import taboolib.module.kether.*
import taboolib.module.kether.scriptParser

object ActionDialog {

    /*
     * dialog select [dialogID]
     * dialog send (where [location])
     * dialog end
     */
    internal object Parser {
        @KetherParser(["dialog"], shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("select") {
                    val action = it.next(ArgTypes.ACTION)
                    actionNow {
                        newFrame(action).run<Any>().thenAccept { a ->
                            variables().set("@QenDialogID", a.toString())
                        }
                    }
                }
                case("send") {
                    val w = try {
                        mark()
                        expect("where")
                        true
                    }catch (ex: Exception) {
                        reset()
                        false
                    }
                    if (w) {
                        actionNow {
                            val action = it.next(ArgTypes.ACTION)
                            newFrame(action).run<Location>().thenAccept { a ->
                                DialogManager.sendDialog(player(), selectDialogID(), a)
                            }
                        }
                    }else {
                        actionNow {
                            DialogManager.sendDialog(player(), selectDialogID())
                        }
                    }
                }
                case("end") {
                    actionNow {
                        DialogManager.endHoloDialog(player(), selectDialogID())
                    }
                }
            }
        }
    }

}