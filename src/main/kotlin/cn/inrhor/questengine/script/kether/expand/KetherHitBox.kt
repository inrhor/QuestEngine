package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.utlis.location.FixedHoloHitBox
import cn.inrhor.questengine.utlis.location.LocationTool
import taboolib.library.kether.*
import taboolib.module.kether.KetherParser
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
) : QuestAction<FixedHoloHitBox>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<FixedHoloHitBox>? {
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

    companion object {
        @KetherParser(["hitBox"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            val offset = LocationTool().getOffsetType(it)
            val multiply = it.nextDouble()
            val height = it.nextDouble()
            val minX = it.nextDouble()
            val maxX = it.nextDouble()
            val minY = it.nextDouble()
            val maxY = it.nextDouble()
            val minZ = it.nextDouble()
            val maxZ = it.nextDouble()
            val long = it.nextInt()
            val itemID = it.nextToken()
            val boxY = it.nextDouble()
            KetherHitBox(offset, multiply, height, minX, maxX, minY, maxY, minZ, maxZ, long, itemID, boxY)
        }
    }
}