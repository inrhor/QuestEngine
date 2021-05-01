package cn.inrhor.questengine.common.hologram

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.dialog.cube.DialogCube
import cn.inrhor.questengine.common.hologram.asi.HoloManagerID
import cn.inrhor.questengine.common.hologram.asi.HoloTextASI
import cn.inrhor.questengine.common.kether.KetherHandler
import cn.inrhor.questengine.utlis.location.LocationTool
import org.bukkit.Location
import org.bukkit.entity.Player

class HoloDialog(
    var holoID: String,
    var dialogCube: DialogCube,
    var npcLoc: Location,
    var viewers: MutableSet<Player>) {

    constructor(dialogCube: DialogCube, npcLoc: Location, viewers: MutableSet<Player>) :
            this(dialogCube.dialogID, dialogCube, npcLoc, viewers)

    private val textASIs: MutableList<HoloTextASI> = mutableListOf()

    fun run() {
        var holoLoc = npcLoc
        var nextY = 0.0
        var dialogTextIndex = 0
        for (i in dialogCube.dialog) {
            val iC = i.toUpperCase()
            when {
                iC.startsWith("INITLOC") -> {
                    holoLoc = LocationTool().getFixedLoc(
                        npcLoc, KetherHandler.evalFixedLoc(i))
                }
                iC.startsWith("ADDLOC") -> {
                    holoLoc = LocationTool().getFixedLoc(
                        holoLoc, KetherHandler.evalFixedLoc(i))
                }
                iC.startsWith("NEXTY") -> {
                    nextY = iC.substring(0, i.indexOf("NEXTY ")).toDouble()
                    holoLoc.add(0.0, nextY, 0.0)
                }
                iC.startsWith("FRAME") -> {

                }
                iC.startsWith("TEXT") -> {
                    val entityID = HoloManagerID().generate(
                        holoID, "text", dialogTextIndex, "")
                    val holoTextASI = HoloTextASI(
                        entityID, viewers,
                        dialogCube.textAnimation.getTextContent(dialogTextIndex),
                        holoLoc,
                        dialogTextIndex
                    )
                    dialogTextIndex++
                    textASIs.add(holoTextASI)
                }
                iC.startsWith("ITEMNORMAL") -> {

                }
                iC.startsWith("ITEM") -> {

                }
            }
        }
    }

    /**
     * 启动玩家点击框检查器
     */
    fun startClickTask() {
        for (player in viewers) {
            val uuid = player.uniqueId
            val pData = DataStorage.playerDataStorage[uuid]!!
            val boxData = pData.clickBoxData
            boxData.clickBoxList = pData.clickBoxList
            boxData.startClickTask()
        }
    }



}