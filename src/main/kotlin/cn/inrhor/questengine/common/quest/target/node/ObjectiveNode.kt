package cn.inrhor.questengine.common.quest.target.node

import org.bukkit.Location

class ObjectiveNode(
    // 完成次数
    var amount: Int = 0,
    // 数量
    var number: Int = 0,
    // 材料列表
    var material: List<String> = emptyList(),
    // id列表
    var id: List<String> = emptyList(),
    // 坐标
    var location: Location? = null,
    // 经验
    var exp: Int = 0,
    // 物品
    var item: List<String> = emptyList(),
    // 矩阵
    var matrix: List<String> = emptyList(),
    // 花费
    var cost: Double = 0.0,
    // 实体类型
    var entityTypes: List<String> = emptyList(),
    // 名字
    var name: List<String> = emptyList(),
    // 内容
    var content: List<String> = emptyList(),
    // 原因
    var cause: List<String> = emptyList())