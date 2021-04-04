package cn.inrhor.questengine.common.dialog.cube

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.utlis.public.UseString
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.Location
import org.bukkit.entity.Player

class ClickBoxUtil {

    /**
     * 发送交互点击框
     */
    fun sendClickBox(viewers: MutableSet<Player>, boxLoc: Location, replyCube: ReplyCube) {
        val clickBox = ClickBox(boxLoc, replyCube)
        viewers.forEach{
            val playerData = DataStorage.playerDataStorage[it.uniqueId]!!
            playerData.clickBoxList.add(clickBox)
        }
    }

}