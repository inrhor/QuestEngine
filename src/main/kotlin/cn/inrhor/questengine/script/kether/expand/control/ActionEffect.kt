package cn.inrhor.questengine.script.kether.expand.control

import cn.inrhor.questengine.script.kether.player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.module.kether.KetherParser
import taboolib.module.kether.actionNow
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

object ActionEffect {

    @KetherParser(["actionEffect"], shared = true)
    fun parser() = scriptParser {
        it.switch {
            case("select") {
                val effect = it.next(ArgTypes.ACTION)
                it.mark()
                it.expect("time")
                val time = it.next(ArgTypes.ACTION)
                actionNow {
                    newFrame(effect).run<Any>().thenAccept { a ->
                        val ef = PotionEffectType.getByName(a.toString().uppercase())?:return@thenAccept
                        newFrame(time).run<Any>().thenAccept { b ->
                            player().addPotionEffect(PotionEffect(ef, Coerce.toInteger(b), 1))
                        }
                    }
                }
            }
        }
    }

}