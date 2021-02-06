package cn.inrhor.questengine.common.hologram

import cn.inrhor.questengine.api.hologram.IHologramManager
import cn.inrhor.questengine.common.dialog.holo.DialogHolo
import cn.inrhor.questengine.common.nms.NMS
import cn.inrhor.questengine.utlis.public.MsgUtil
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class IHolo(
    var holoID: String,
    var textLoc: Location,
    var itemLoc: Location,

    var viewers: MutableSet<Player>,

    // 实现动画方法：通过外界更改指定行的内容
    var textList: MutableList<String> = mutableListOf(),
    var itemList: MutableList<ItemStack> = mutableListOf()
) {

    constructor(holoID: String, textLoc: Location, itemLoc: Location, viewers: MutableSet<Player>) :
            this(holoID, textLoc, itemLoc, viewers, mutableListOf(), mutableListOf())

    private var hasInit: Boolean = false

    private val textEntityIDs: MutableList<Int> = mutableListOf()
    private val itemEntityIDs: MutableList<Int> = mutableListOf()

    /**
     * 初始化
     */
    fun init() {
        if (hasInit) return

        repeat(textList.size) {
            addEntityID("text", it)
        }
        repeat(itemList.size) {
            addEntityID("item", it)
        }

        sendTextHolo(viewers)
        sendItemHolo(viewers)
        val dialogHolo = DialogHolo(this, viewers)
        dialogHolo.runRunnable()

        hasInit = true

        IHologramManager().addHolo(holoID, this)
    }

    private fun addEntityID(type: String, index: Int) {
        val strID = "$holoID-$type-$index"
        val entityID = strID.hashCode()
        if (IHologramManager().existHoloEntityID(entityID)) {
            // say
        }
        when (type) {
            "text" -> {
                textEntityIDs.add(entityID)
            }
            "item" -> {
                itemEntityIDs.add(entityID)
            }
            else -> {
                MsgUtil.send("null type")
                return
            }
        }
        IHologramManager().addHoloEntityID(entityID)
    }

    /**
     * 更新全息视觉
     */
    fun update() {
        sendTextHolo(viewers)
        sendItemHolo(viewers)
    }

    /**
     * 更新全息内容
     */
    fun updateContent(line: Int) {
        textEntityIDs.forEach {
            getPackets().updateDisplayName(viewers, it, textList[line])
        }
    }

    /**
     * 添加可视者
     */
    fun addViewer(player: Player) {
        viewers.add(player)
    }

    /**
     * 删除可视者并销毁他的全息
     */
    fun removeViewer(player: Player) {
        viewers.remove(player)
        if (!player.isOnline) return
        textEntityIDs.forEach {
            getPackets().destroyEntity(player, it)
        }
        itemEntityIDs.forEach {
            getPackets().destroyEntity(player, it)
        }
    }

    /*var follow : Boolean? = false
    var distance : Double? = 0.0

    fun move() {

        *//*viewers?.forEach {
            THologram.create(location, contentList?.get(0), it)
        }*//*
    }*/

    private fun sendTextHolo(players: MutableSet<Player>) {
        sendHolo(players, "text")
    }

    private fun sendItemHolo(players: MutableSet<Player>) {
        sendHolo(players, "item")
    }

    private fun sendHolo(players: MutableSet<Player>,
                             type: String) {
        /*if (holoEntityIDMap.containsKey(id)) {
            // Msg, id不存在消息
            return
        }*/

        var index = 0
        var entityIDs = textEntityIDs
        var entityLoc = textLoc
        if (type == "item") {
            entityIDs = itemEntityIDs
            entityLoc = itemLoc
        }
        entityIDs.forEach {
            getPackets().spawnAS(players, it, entityLoc)

            entityLoc.add(0.0, -0.25, 0.0)

            if (type == "text") {
                getPackets().initAS(players, it, showName = true, isSmall = true, marker = true)
                if (textList.isNotEmpty() && textList.size > index) {
                    getPackets().updateDisplayName(players, it, textList[index])
                }else return
            }else {
                getPackets().initAS(players, it, showName = false, isSmall = true, marker = true)
                if (itemList.isNotEmpty() && itemList.size > index) {
                    // 生成物品实体
                    val itemInt = Random().nextInt()
                    getPackets().spawnItem(players, itemInt, entityLoc, itemList[index])
                    // 物品实体骑乘到盔甲架
                    getPackets().updatePassengers(players, it, itemInt)
                } else return
            }

            index++
        }
    }

    private fun getPackets(): NMS {
        return NMS.INSTANCE
    }

}