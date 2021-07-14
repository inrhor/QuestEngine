package cn.inrhor.questengine.common.database

import org.bukkit.entity.Player

abstract class Database {

    /**
     * 为玩家拉取数据
     */
    abstract fun pull(player: Player)

    /**
     * 为玩家上载数据
     */
    abstract fun push(player: Player)

}