package cn.inrhor.imipetcore.common.hook.invero

import cc.trixey.invero.common.Object
import cc.trixey.invero.common.sourceObject
import cc.trixey.invero.core.Context
import cn.inrhor.imipetcore.common.database.data.PetData
import cn.inrhor.imipetcore.common.database.data.SkillData

object InvGenerator {

    fun petGenerate(context: Context, uiType: UiTypePet): List<Object> {
        val player = context.player

        return uiType.list(player).map {
            sourceObject {
                // https://invero.trixey.cc/docs/advance/basic/context
                // context set pet_data to element self_pet
                put("self_pet", it) // 目的传递给下一个菜单
                put("name", it.name)
            }
        }
    }

    fun skillGenerate(context: Context, uiType: UiTypeSkill): List<Object> {
        val petData = context.variables["pet_data"] as PetData
        var index = 0
        return uiType.list(petData).map {
            sourceObject {
                put("self_skill", it) // 目的传递给下一个菜单
                put("index", index)
                put("id", it.id)
                index++
            }
        }
    }

    fun skillTreeGenerate(context: Context, uiType: UiTypeSkill): List<Object> {
        val skillData = context.variables["pet_skill"] as SkillData

        return uiType.list(skillData).map {
            sourceObject {
                put("id", it.id)
            }
        }
    }

}