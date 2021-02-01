package cn.inrhor.questengine.common.hologram

import cn.inrhor.questengine.api.nms.NMS
import cn.inrhor.questengine.common.dialog.holo.DialogHolo
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class IHolo(
    val holoID: String,
    val location: Location,

    val viewers: MutableSet<Player> = mutableSetOf(),

    // 实现动画方法：通过外界更改指定行的内容
    val textList: MutableList<String> = mutableListOf(),
    val itemList: MutableList<ItemStack> = mutableListOf(),

    val canClick: Boolean
) {

    constructor(holoID: String, location: Location, viewers: MutableSet<Player>, canClick: Boolean) :
            this(holoID, location, viewers, mutableListOf(), mutableListOf(), canClick)
    constructor(holoID: String, location: Location, viewers: MutableSet<Player>, textList: MutableList<String>, itemList: MutableList<ItemStack>) :
            this(holoID, location, viewers, textList, itemList, false)

    private var hasInit: Boolean = false

    private val entityIDs: MutableList<Int> = mutableListOf()

    /*val text: String = ""
    val item: ItemStack = ItemStack(Material.AIR)*/

    /**
     * 初始化
     */
    fun init() {
        if (hasInit) return

        repeat(textList.size) {
            addEntityID()
        }
        repeat(itemList.size) {
            addEntityID()
        }

        sendHolo(viewers, holoID, location, textList, itemList, entityIDs, canClick)
        val dialogHolo = DialogHolo(viewers)
        dialogHolo.runRunnable()

        hasInit = true
    }

    private fun addEntityID() {
        val entityID = randomIntEntityID.nextInt()
        entityIDs.add(entityID)
    }

    /**
     * 更新全息的内容
     */
    fun update() {
        sendHolo(viewers, holoID, location, textList, itemList, entityIDs, canClick)
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
        entityIDs.forEach {
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

    private fun sendHolo(players: MutableSet<Player>,
                         id: String,
                         loc: Location,
                         textList: MutableList<String>,
                         itemList: MutableList<ItemStack>,
                         entityIDs: MutableList<Int>,
                         canClick: Boolean) {
        /*if (holoEntityIDMap.containsKey(id)) {
            // Msg, id不存在消息
            return
        }

        val holoIds : MutableList<Int> = ArrayList()
        holoEntityIDMap[id] = holoIds*/

/*        for ((index) in textList.withIndex()) {
//            val entityID = randomIntEntityID.nextInt()

            spawnAS(playerList, entityID, loc)

            loc.add(0.0, -0.22, 0.0)

            setMetadata(playerList, entityID)

            setText(playerList, entityID, textList[index])

//            if (itemList.size > index) {setItem(playerList, entityID, itemList[index])}

            *//*if (canClick) {
                val ids = holoEntityIDMap[id]!!
                ids.add(entityID)
                holoEntityIDMap[id] = ids
            }*//*
        }*/

        var index = 0;
        entityIDs.forEach {
            //            spawnAS(playerList, it, loc)
            getPackets().spawnAS(players, it, loc)

            loc.add(0.0, -0.22, 0.0)

//            setMetadata(playerList, it, canClick)
            getPackets().initAS(players, it, !canClick, canClick)

            if (textList.isNotEmpty()) {
                getPackets().updateDisplayName(players, it, textList[index])
            }else {
                val itemInt = randomIntEntityID.nextInt()
                getPackets().spawnItem(players, itemInt, loc, itemList[index])
                getPackets().updatePassengers(players, it, itemInt)
            }

            index++
        }
    }

    private fun getPackets(): NMS {
        return NMS.INSTANCE
    }

    // 防止EntityID相似而冲突
    companion object {
        // 给定随机entityID
        @JvmStatic
        val randomIntEntityID = Random()

        // holoID 对应 entityID集 作为触发交互式脚本 标识
        /*@JvmStatic
        var holoEntityIDMap = mutableMapOf<String, MutableList<Int>>()*/
    }

}