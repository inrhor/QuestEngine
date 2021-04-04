package cn.inrhor.questengine.common.dialog.cube

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.nms.NMS
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class PlayerClickBoxData(
    private val uuid: UUID,
    var entityID: Int,
    var itemID: Int,
    var clickBoxList: MutableList<ClickBox>,
    var clickRunnable: BukkitRunnable?,
    var hasSpawnASI: Boolean,
    var hasSpawnItem: Boolean
) {
    constructor(uuid: UUID, clickBoxList: MutableList<ClickBox>):
            this(uuid, ("asi$uuid").hashCode(), ("item$uuid").hashCode(),
                clickBoxList, null,
                false, false)

    fun startClickTask() {
        if (clickRunnable != null) return

        if (Bukkit.getPlayer(uuid) == null) return
        val player = Bukkit.getPlayer(uuid)!!

        val viewer = mutableSetOf(player)

        if (!hasSpawnASI) {
            getPackets().spawnAS(viewer, entityID, player.location)
            getPackets().initAS(viewer, entityID, showName = false, isSmall = true, marker = true)
        }

        clickRunnable = object : BukkitRunnable() {
            override fun run() {
                if (!player.isOnline) {
                    destroy(player)
                    cancel()
                    return
                }

                if (!isLookingBox(viewer)) {
                    getPackets().destroyEntity(player, itemID)
                    hasSpawnItem = false
                }
            }
        }
        (clickRunnable as BukkitRunnable).runTaskTimer(QuestEngine.plugin, 0, 10L)
    }

    fun destroy(player: Player) {
        getPackets().destroyEntity(player, itemID)
        hasSpawnItem = false
        getPackets().destroyEntity(player, entityID)
        hasSpawnASI = false
    }

    fun isLookingBox(viewer: MutableSet<Player>): Boolean {
        for (box in clickBoxList) {
            if (box.isClick(uuid)) {
                val loc = box.boxLoc
                if (!hasSpawnItem) {
                    val replyCube = box.replyCube
                    getPackets().spawnItem(
                        viewer, itemID,
                        loc.add(0.0, replyCube.showAddY, 0.0),
                        replyCube.showItem)
                    getPackets().updatePassengers(viewer, entityID, itemID)
                }
                getPackets().updateLocation(viewer, entityID, loc)
                return true
            }
        }
        return false
    }

    fun stopClickTask() {
        if (clickRunnable != null) {
            destroy(Bukkit.getPlayer(uuid)!!)
            clickRunnable!!.cancel()
        }
    }

    private fun getPackets(): NMS {
        return NMS.INSTANCE
    }
}