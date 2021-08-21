package cn.inrhor.questengine.common.packet

import cn.inrhor.questengine.api.packet.*
import cn.inrhor.questengine.common.database.data.PacketData
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.item.ItemManager
import cn.inrhor.questengine.utlis.file.GetFile
import cn.inrhor.questengine.utlis.UtilString
import cn.inrhor.questengine.utlis.location.LocationTool
import org.bukkit.Bukkit
import org.bukkit.Location
import taboolib.library.configuration.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.platform.function.*
import taboolib.module.lang.sendLang
import java.io.File

object PacketManager {

    val packetMap = mutableMapOf<String, PacketModule>()

    fun register(packetID: String, packetModule: PacketModule) {
        packetMap[packetID] = packetModule
    }

    fun removeID(packetID: String) {
        packetMap.remove(packetID)
    }

    fun getPacketDataList(player: Player, packetID: String): MutableList<PacketData> {
        val pData = DataStorage.getPlayerData(player)
        if (pData.dataPacket.containsKey(packetID)) return pData.dataPacket[packetID]!!
        return mutableListOf()
    }

    fun getPacketData(player: Player, entityID: Int): PacketData? {
        val pData = DataStorage.getPlayerData(player)
        pData.dataPacket.values.forEach {
            it.forEach { p ->
                if (p.entityID == entityID) return p
            }
        }
        return null
    }

    fun addDataPacket(player: Player, packetID: String, dataPackets: MutableList<PacketData>) {
        val pData = DataStorage.getPlayerData(player)
        pData.addDataPacket(packetID, dataPackets)
    }

    fun removePacketEntity(player: Player, packetID: String, entityID: Int) {
        val pData = DataStorage.getPlayerData(player)
        pData.dataPacket.forEach { (id, data) ->
            for (i in data.count() -1 downTo 0) {
                val d = data[i]
                if (packetID == id && entityID == d.entityID) {
                    destroyEntity(player, d.entityID)
                    data.remove(d)
                }
            }
        }
    }

    fun removePacketEntity(player: Player, packetID: String, location: Location, range: Double) {
        val pData = DataStorage.getPlayerData(player)
        pData.dataPacket.forEach { (id, data) ->
            for (i in data.count() -1 downTo 0) {
                val d = data[i]
                if (packetID == id && LocationTool.inLoc(location, d.location, range)) {
                    destroyEntity(player, d.entityID)
                    data.remove(d)
                }
            }
        }
    }

    fun sendThisPacket(packetModule: PacketModule, entityID: Int, sender: Player, location: Location) {
        val viewers = mutableSetOf(sender)
        if (packetModule.viewer == "all") viewers.addAll(Bukkit.getOnlinePlayers())
        sendMetaPacket(entityID, packetModule, viewers, location)
    }

    fun sendThisPacket(packetModule: PacketModule, sender: Player, location: Location, dataPacketID: DataPacketID) {
        sendThisPacket(packetModule, dataPacketID.getEntityID(), sender, location)
    }

    private fun sendMetaPacket(entityID: Int, packetModule: PacketModule, viewers: MutableSet<Player>, location: Location) {
        spawnEntity(viewers, entityID, packetModule.entityType, location)
        packetModule.mate.forEach {
            val sp = it.split(" ")
            val sign = sp[0].lowercase()
            if (sign == "equip") {
                val slot = sp[1].uppercase()
                val itemID = sp[2]
                val item = ItemManager.get(itemID)
                updateEquipmentItem(viewers, entityID, EquipmentSlot.valueOf(slot), item)
            }else if (sign == "displayName") { // false 为 不显示
                val displayName = sp[1]
                updateDisplayName(viewers, entityID, displayName)
                val display = sp[2].toBoolean()
                setEntityCustomNameVisible(viewers, entityID, display)
            }else if (sign == "visible") {
                if (!sp[1].toBoolean()) {
                    isInvisible(viewers, entityID)
                }
            }
        }
    }

    /**
     * 加载并注册数据包文件
     */
    fun loadPacket() {
        val packetFolder = GetFile.getFile("space/packet/", "PACKET-NO_FILES", true)
        GetFile.getFileList(packetFolder).forEach{
            checkRegPacket(it)
        }
    }

    /**
     * 检查和注册数据包
     */
    private fun checkRegPacket(file: File) {
        val yaml = YamlConfiguration.loadConfiguration(file)
        if (yaml.getKeys(false).isEmpty()) {
            console().sendLang("PACKET-EMPTY_CONTENT", UtilString.pluginTag, file.name)
            return
        }
        for (packetID in yaml.getKeys(false)) {
            PacketFile.init(yaml.getConfigurationSection(packetID))
        }
    }

    /*fun returnItemEntityID(packetID: String, mate: MutableList<String>): MutableMap<String, Int> {
        val itemEntityID = mutableMapOf<String, Int>()

        mate.forEach {
            if (it.lowercase().startsWith("equip ")) {
                val sp = it.split(" ")
                val itemID = sp[2]
                val entityID = generate(packetID, itemID)
                itemEntityID[itemID] = entityID
            }
        }

        return itemEntityID
    }*/

}