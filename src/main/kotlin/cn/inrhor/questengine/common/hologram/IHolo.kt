package cn.inrhor.questengine.common.hologram

import cn.inrhor.questengine.api.hologram.IHologramManager
import cn.inrhor.questengine.common.dialog.cube.DialogCube
import cn.inrhor.questengine.common.dialog.holo.DialogHolo
import cn.inrhor.questengine.common.dialog.location.LocationTool
import cn.inrhor.questengine.common.nms.NMS
import cn.inrhor.questengine.utlis.public.MsgUtil
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.HashMap
import java.util.LinkedHashMap

class IHolo(
    var holoID: String,
    var dialogCube: DialogCube,
    var npcLoc: Location,

    var viewers: MutableSet<Player>,

    // 实现动画方法：通过外界更改指定行的内容
    var textList: MutableList<String> = mutableListOf(),
    var itemList: MutableList<ItemStack> = mutableListOf()
) {

    constructor(dialogCube: DialogCube, npcLoc: Location, viewers: MutableSet<Player>) :
            this(dialogCube.dialogID, dialogCube, npcLoc, viewers,
                dialogCube.ownTextInitContent, dialogCube.ownItemInitContent.getDialogItemList())

    private var hasInit: Boolean = false
    var hasSendReply: Boolean = false

    private val textEntityIDs: MutableList<Int> = mutableListOf()
    private val itemEntityIDs: MutableList<Int> = mutableListOf()
    private val itemStackEntityIDs: MutableList<Int> = mutableListOf()

    private val textReplyEntityIDMap: HashMap<String, MutableList<Int>> = LinkedHashMap()
    private val itemReplyEntityIDMap: HashMap<String, MutableList<Int>> = LinkedHashMap()
    private val stackReplyEntityIDMap: HashMap<String, MutableList<Int>> = LinkedHashMap()

    /**
     * 初始化
     */
    fun init() {
        if (hasInit) return

        repeat(textList.size) {
            addEntityID("text", it)
        }
        repeat(itemList.size) {
            addEntityID("itemStack", it)
        }
        repeat(itemList.size) {
            addEntityID("item", it)
        }

        sendTextHolo()
        sendItemHolo()

        val dialogHolo = DialogHolo(this, viewers, dialogCube)
        dialogHolo.runRunnable()

        hasInit = true

        IHologramManager().addHolo(holoID, this)
    }

    /*
     * type 分别有 text(盔甲架) item(盔甲架) itemStack(物品)
     * replyText replyItem replyStack
     */
    private fun addEntityID(type: String, index: Int, replyID: String = "") {
        val strID = "$holoID-$type-$index$replyID"
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
            "itemStack" -> {
                itemStackEntityIDs.add(entityID)
            }
            "replyText" -> {
                if (!textReplyEntityIDMap.containsKey(replyID)) {
                    textReplyEntityIDMap[replyID] = mutableListOf()
                }
                textReplyEntityIDMap[replyID]!!.add(entityID)
            }
            "replyItem" -> {
                if (!itemReplyEntityIDMap.containsKey(replyID)) {
                    itemReplyEntityIDMap[replyID] = mutableListOf()
                }
                itemReplyEntityIDMap[replyID]!!.add(entityID)
            }
            "replyStack" -> {
                if (!stackReplyEntityIDMap.containsKey(replyID)) {
                    stackReplyEntityIDMap[replyID] = mutableListOf()
                }
                stackReplyEntityIDMap[replyID]!!.add(entityID)
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
        sendTextHolo()
        sendItemHolo()
    }

    /**
     * 更新全息内容
     */
    fun updateContent() {
        for (i in 0 until textEntityIDs.size) {
            getPackets().updateDisplayName(viewers, textEntityIDs[i], textList[i])
        }
        for (i in 0 until itemEntityIDs.size) {
            getPackets().updateEntityMetadata(
                viewers,
                itemStackEntityIDs[i],
                getPackets().getMetaEntityItemStack(itemList[i]))
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

    private fun sendTextHolo() {
        sendDialogHolo("text")
    }

    private fun sendItemHolo() {
        sendDialogHolo("item")
    }

    private fun sendItemStack() {
        sendDialogHolo("itemStack")
    }



    private fun sendDialogHolo(type: String) {
        /*if (holoEntityIDMap.containsKey(id)) {
            // Msg, id不存在消息
            return
        }*/

        var index = 0
        var entityIDs = textEntityIDs
        var entityLoc = LocationTool().getFixedLoc(npcLoc, dialogCube.ownTextLoc)
        if (type == "item") {
            entityIDs = itemEntityIDs
            entityLoc = LocationTool().getFixedLoc(npcLoc, dialogCube.ownItemLoc)
        }
        entityIDs.forEach {
            getPackets().spawnAS(viewers, it, entityLoc)

            entityLoc.add(0.0, -0.25, 0.0)

            if (type == "text") {
                getPackets().initAS(viewers, it, showName = true, isSmall = true, marker = true)
                if (textList.isNotEmpty() && textList.size > index) {
                    getPackets().updateDisplayName(viewers, it, textList[index])
                }else return
            }else {
                getPackets().initAS(viewers, it, showName = false, isSmall = true, marker = true)
            }
            index++
        }
    }
    fun sendReplyHolo() {
        hasSendReply = true
        dialogCube.replyCubeList.forEach{ replyCube ->
            for (index in 0 until replyCube.textContent.size) {
                val textFixedLoc = replyCube.textAddLoc
                val textLoc = LocationTool().getFixedLoc(npcLoc, textFixedLoc)
                val content = replyCube.textContent
                if (content.isNotEmpty() && content.size > index) {
                    addEntityID("replyText", index, replyCube.replyID)
                    val entityID = textReplyEntityIDMap[replyCube.replyID]!![index]
                    getPackets().spawnAS(viewers, entityID, textLoc.add(0.0, -index*0.25, 0.0))
                    getPackets().initAS(viewers, entityID, showName = true, isSmall = true, marker = true)
                    getPackets().updateDisplayName(viewers, entityID, content[index])
                }
            }
            for (index in 0 until replyCube.itemContent.getDialogItemList().size) {
                val itemFixedLoc = replyCube.itemAddLoc
                val itemLoc = LocationTool().getFixedLoc(npcLoc, itemFixedLoc)
                val content = replyCube.itemContent
                val replyItemList = content.getDialogItemList()
                if (replyItemList.isNotEmpty() && replyItemList.size > index) {
                    addEntityID("replyItem", index, replyCube.replyID)
                    addEntityID("replyStack", index, replyCube.replyID)
                    val entityID = itemReplyEntityIDMap[replyCube.replyID]!![index]
                    getPackets().spawnAS(viewers, entityID, itemLoc.add(0.0, -index*0.3, 0.0))
                    val stackInt = stackReplyEntityIDMap[replyCube.replyID]!![index]
                    getPackets().spawnItem(viewers, stackInt, itemLoc, replyCube.getTheLineItem(index).item)
                    getPackets().initAS(viewers, entityID, showName = false, isSmall = true, marker = true)
                    getPackets().updatePassengers(viewers, entityID, stackInt)
                }
            }
        }
    }

    fun spawnItem(line: Int) {
        val itemStackInt = itemStackEntityIDs[line]
        getPackets().spawnItem(viewers, itemStackInt, npcLoc, itemList[line])

        // 物品实体骑乘到盔甲架
        getPackets().updatePassengers(viewers, itemEntityIDs[line], itemStackInt)
    }

    private fun getPackets(): NMS {
        return NMS.INSTANCE
    }

}