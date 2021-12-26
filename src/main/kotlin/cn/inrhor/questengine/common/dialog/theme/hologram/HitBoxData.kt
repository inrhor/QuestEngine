package cn.inrhor.questengine.common.dialog.theme.hologram

import cn.inrhor.questengine.api.dialog.theme.ItemPlay
import cn.inrhor.questengine.utlis.location.BoundingBox
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class HitBoxData(
    var offset: Float = 0F,
    var multiply: Double = 0.0,
    var height: Double = 0.0,
    var long: Double = 0.0,
    var itemStack: ItemStack = ItemStack(Material.STONE),
    var type: ItemPlay.Type = ItemPlay.Type.FIXED,
    var boxY: Double = 0.0,
    var hitBox: BoundingBox = BoundingBox.initHitBox()
) {
}