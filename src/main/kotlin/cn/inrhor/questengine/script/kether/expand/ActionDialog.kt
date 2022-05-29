package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.common.dialog.DialogManager
import cn.inrhor.questengine.script.kether.ActionSelect
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
        @KetherParser(["dialog"])
        fun parser() = scriptParser {
            it.switch {
                case("select") {
                    actionNow {
                        newFrame(it.next(ArgTypes.ACTION)).run<Any>().thenAccept { a ->
                            variables().set(ActionSelect.ID.variable[5], a.toString())
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
                    actionNow {
                        if (w) {
                            newFrame(it.next(ArgTypes.ACTION)).run<Location>().thenAccept { a ->
                                DialogManager.sendDialog(player(), selectDialogID(), a)
                            }
                        }else DialogManager.sendDialog(player(), selectDialogID())
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