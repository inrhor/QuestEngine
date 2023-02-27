package cn.inrhor.questengine.common.item

import cn.inrhor.questengine.script.kether.evalString
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.module.kether.ScriptContext
import taboolib.platform.util.buildItem

data class ItemElement(
    var material: String = "APPLE",
    var name: String = "", var lore: List<String> = listOf(), var modelData: Int = 0, val data: List<String> = listOf()) {

    fun itemStack(player: Player, variable: (ScriptContext) -> Unit): ItemStack = buildItem(XMaterial.valueOf(material)) {
        val a = this@ItemElement
        name = player.evalString(a.name, "{{", "}}") {
            variable(it)
        }
        a.lore.forEach {
            lore.add(player.evalString(it, "{{", "}}"){ s ->
                variable(s)
            })
        }
        customModelData = modelData
    }

    fun itemStack(player: Player): ItemStack = itemStack(player) {}
}