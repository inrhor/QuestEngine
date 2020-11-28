package cn.inrhor.questengine.common.hologram

import org.bukkit.Location
import org.bukkit.entity.Player

class IHolo(
    val holoID: String,
    val viewers: MutableList<Player>,
    val contentList: MutableList<String>,
    val location: Location
) {



    /*var follow : Boolean? = false
    var distance : Double? = 0.0

    fun move() {

        *//*viewers?.forEach {
            THologram.create(location, contentList?.get(0), it)
        }*//*
    }*/

}