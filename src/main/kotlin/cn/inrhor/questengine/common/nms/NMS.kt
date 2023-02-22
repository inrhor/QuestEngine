package cn.inrhor.questengine.common.nms

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.nmsProxy

abstract class NMS {

    abstract fun entityRotation(players: MutableSet<Player>, entityId: Int, yaw: Float)

    abstract fun spawnEntity(players: MutableSet<Player>, entityId: Int, entityType: String, location: Location)

    /**
     * 生成盔甲架
     */
    abstract fun spawnAS(players: MutableSet<Player>, entityId: Int, location: Location)

    /**
     * 生成物品
     */
    abstract fun spawnItem(players: MutableSet<Player>, entityId: Int, location: Location, itemStack: ItemStack)

    /**
     * 删除实体
     */
    abstract fun destroyEntity(player: Player, entityId: Int)

    /**
     * 删除实体
     */
    abstract fun destroyEntity(players: MutableSet<Player>, entityId: Int)

    /**
     * 更新名称
     */
    abstract fun updateDisplayName(players: MutableSet<Player>, entityId: Int, name: String)

    /**
     * 更新名称
     */
    abstract fun updateDisplayName(player: Player, entityId: Int, name: String)

    /**
     * 更新位置
     */
    abstract fun updateLocation(players: MutableSet<Player>, entityId: Int, location: Location)

    /**
     * 更新乘客
     */
    abstract fun updatePassengers(players: MutableSet<Player>, entityId: Int, vararg passengers: Int)

    /**
     * 更新实体头部物品
     */
    abstract fun updateEquipmentItem(players: MutableSet<Player>, entityId: Int, slot: EquipmentSlot, zitemStack: ItemStack)

    /**
     * 更新实体元数据
     */
    abstract fun updateEntityMetadata(players: MutableSet<Player>, entityId: Int, vararg objects: Any)

    /**
     * 更新实体元数据
     */
    abstract fun updateEntityMetadata(player: Player, entityId: Int, vararg objects: Any)

    /**
     * 物品
     */
    abstract fun getMetaEntityItemStack(itemStack: ItemStack): Any

    /**
     * 重力
     */
    abstract fun getMetaEntityGravity(noGravity: Boolean): Any

    /**
     * 静音
     */
    abstract fun getMetaEntitySilenced(silenced: Boolean): Any

    /**
     * 名称可见
     */
    abstract fun getMetaEntityCustomNameVisible(visible: Boolean): Any

    /**
     * 名称
     */
    abstract fun getMetaEntityCustomName(name: String): Any

    /**
     * 已设置好的盔甲架基本值
     */
    abstract fun getMetaASProperties(isSmall: Boolean, marker: Boolean): Any

    /**
     * 初始化盔甲架
     *
     * @param marker true为关闭碰撞箱
     */
    abstract fun initAS(players: MutableSet<Player>, entityId: Int, showName: Boolean, isSmall: Boolean, marker: Boolean)

    /**
     * 不可见
     */
    abstract fun getIsInvisible(): Any

    abstract fun camera(player: Player, entityId: Int)

    companion object {

        val INSTANCE by lazy {
            nmsProxy<NMS>()
        }
    }
}