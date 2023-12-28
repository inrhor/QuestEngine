package cn.inrhor.questengine.api.manager

import cn.inrhor.questengine.api.event.StorageEvent
import cn.inrhor.questengine.api.manager.DataManager.storage
import org.bukkit.entity.Player

object StorageManager {

    /**
     * 设置键值对数据
     */
    fun Player.setStorage(key: String, value: String) {
        StorageEvent.Set(this, key, value).call()
    }

    /**
     * 删除键值对数据
     */
    fun Player.delStorage(key: String) {
        StorageEvent.Remove(this, key).call()
    }

    /**
     * 获取键值对数据
     */
    fun Player.getStorageValue(key: String): String {
        return storage()[key]?: "null"
    }

}