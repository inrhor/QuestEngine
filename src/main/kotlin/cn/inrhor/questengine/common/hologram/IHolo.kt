package cn.inrhor.questengine.common.hologram

import cn.inrhor.questengine.common.dialog.holo.DialogHolo
import cn.inrhor.questengine.common.hologram.packets.PacketHolo
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class IHolo(
    val holoID: String,
    val location: Location,

    val viewers: MutableList<Player> = mutableListOf(),

    // 实现动画方法：通过外界更改指定行的内容
    val textList: MutableList<String> = mutableListOf(),
    val itemList: MutableList<ItemStack> = mutableListOf(),

    val canClick: Boolean
) {

    constructor(holoID: String, location: Location, viewers: MutableList<Player>, canClick: Boolean) :
            this(holoID, location, viewers, mutableListOf(), mutableListOf(), canClick)
    constructor(holoID: String, location: Location, viewers: MutableList<Player>, textList: MutableList<String>, itemList: MutableList<ItemStack>) :
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

        PacketHolo().sendHolo(viewers, holoID, location, textList, itemList, entityIDs, canClick)
        val dialogHolo = DialogHolo(viewers)
        dialogHolo.runRunnable()

        hasInit = true
    }

    private fun addEntityID() {
        val entityID = PacketHolo.randomIntEntityID.nextInt()
        entityIDs.add(entityID)
    }

    /**
     * 更新全息的内容
     */
    fun update() {
        PacketHolo().sendHolo(viewers, holoID, location, textList, itemList, entityIDs, canClick)
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
            PacketHolo().destroyAS(player, it)
        }
    }

    /*var follow : Boolean? = false
    var distance : Double? = 0.0

    fun move() {

        *//*viewers?.forEach {
            THologram.create(location, contentList?.get(0), it)
        }*//*
    }*/

}