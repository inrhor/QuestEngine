package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.utlis.location.FixedHoloHitBox
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
    val boxY: Double
) : ScriptAction<FixedHoloHitBox>() {

    override fun run(frame: ScriptFrame): CompletableFuture<FixedHoloHitBox> {
        val fixedHoloHitBox = CompletableFuture<FixedHoloHitBox>()
        fixedHoloHitBox.complete(
            FixedHoloHitBox(
                offset,
                multiply,
                height,
                minX, maxX,
                minY, maxY,
                minZ, maxZ,
                long,
                itemID,
                boxY
            )
        )
        return fixedHoloHitBox
    }


    /*
     * hitBox dir [offset] add [multiply] [height]
     * sizeX [minX] [maxX] sizeY .. sizeZ ..
     * long [long]
     * item [itemID] boxY [boxY]
     */
    internal object Parser {
        @KetherParser(["hitBox"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            it.mark()
            it.expect("dir")
            val offset = LocationTool().getOffsetType(it)
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
            it.expect("boxY")
            val boxY = it.nextDouble()
            KetherHitBox(offset, multiply, height, minX, maxX, minY, maxY, minZ, maxZ, long, itemID, boxY)
        }
    }
}