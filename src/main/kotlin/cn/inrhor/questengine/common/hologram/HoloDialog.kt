package cn.inrhor.questengine.common.hologram

import cn.inrhor.questengine.common.dialog.cube.DialogCube
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

    fun run(type: HoloType) {
        var holoLoc = npcLoc
        var nextY = 0.0
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
                }
                iC.startsWith("HITBOX") -> {
                    if (type != HoloType.REPLY) break

                }
                iC.startsWith("FRAME") -> {
                    if (type != HoloType.DIALOG) break
                }
                iC.startsWith("TEXT") -> {
                    if (type == HoloType.DIALOG) {

                    }else {

                    }
                }
                iC.startsWith("ITEMNORMAL") -> {
                    if (type != HoloType.DIALOG) break
                }
                iC.startsWith("ITEM") -> {
                    if (type != HoloType.REPLY) break
                }
            }
        }
    }

    enum class HoloType{
        DIALOG, REPLY
    }

}