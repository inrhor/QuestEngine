package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.common.dialog.animation.item.ItemDialogPlay
import cn.inrhor.questengine.utlis.location.ReferHoloHitBox
import cn.inrhor.questengine.utlis.location.LocationTool
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class KetherHitBox(
    val offset: Float,
    val multiply: Double,
    val height: Double,
    val minX: Double,
    val maxX: Double,
    val minY: Double,
    val maxY: Double,
    val minZ: Double,
    val maxZ: Double,
    val long: Int,
    val itemID: String,
    val boxType: ItemDialogPlay.Type,
    val boxY: Double
) : ScriptAction<ReferHoloHitBox>() {

    override fun run(frame: ScriptFrame): CompletableFuture<ReferHoloHitBox> {
        val referHoloHitBox = CompletableFuture<ReferHoloHitBox>()
        referHoloHitBox.complete(
            ReferHoloHitBox(
                offset,
                multiply,
                height,
                minX, maxX,
                minY, maxY,
                minZ, maxZ,
                long,
                itemID,
                boxType,
                boxY
            )
        )
        return referHoloHitBox
    }


    /*
     * hitBox dir [offset] add [multiply] [height]
     * sizeX [minX] [maxX] sizeY .. sizeZ ..
     * long [long]
     * item [itemID] use [suspend/fixed] boxY [boxY]
     */
    internal object Parser {
        @KetherParser(["hitBox"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            it.mark()
            it.expect("dir")
            val offset = LocationTool.getOffsetType(it.toString())
            it.mark()
            it.expect("add")
            val multiply = it.nextDouble()
            val height = it.nextDouble()
            it.mark()
            it.expect("sizeX")
            val minX = it.nextDouble()
            val maxX = it.nextDouble()
            it.mark()
            it.expect("sizeY")
            val minY = it.nextDouble()
            val maxY = it.nextDouble()
            it.mark()
            it.expect("sizeZ")
            val minZ = it.nextDouble()
            val maxZ = it.nextDouble()
            it.mark()
            it.expect("long")
            val long = it.nextInt()
            it.mark()
            it.expect("item")
            val itemID = it.nextToken()
            it.mark()
            it.expect("use")
            val itemType = try {
                when (it.nextToken()) {
                    "suspend" -> ItemDialogPlay.Type.SUSPEND
                    else -> ItemDialogPlay.Type.FIXED
                }
            } catch (ignored: Exception) {
                ItemDialogPlay.Type.FIXED
            }
            it.expect("boxY")
            val boxY = it.nextDouble()
            KetherHitBox(offset, multiply, height, minX, maxX, minY, maxY, minZ, maxZ, long, itemID, itemType, boxY)
        }
    }
}