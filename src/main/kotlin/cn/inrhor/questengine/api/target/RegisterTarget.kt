package cn.inrhor.questengine.api.target

import cn.inrhor.questengine.common.quest.target.node.BlockNode
import cn.inrhor.questengine.common.quest.target.node.CauseNode
import cn.inrhor.questengine.common.quest.target.node.IdNode
import cn.inrhor.questengine.script.kether.runEval
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.asLangText

object RegisterTarget {

    @Awake(LifeCycle.LOAD)
    fun loadNode() {
        val block = BlockNode()
        val amount = TargetNode(XMaterial.DIAMOND, "amount", TargetNodeType.INT)
        val number = TargetNode(XMaterial.ORANGE_DYE, "number", TargetNodeType.INT)
        val adyId = IdNode(more = arrayOf("Adyeshach"))
        val npcId = IdNode(more = arrayOf("Citizens"))
        val mobs = TargetNode(XMaterial.ZOMBIE_HEAD, "mobs")
        val need = TargetNode(XMaterial.CYAN_DYE, "need")
        val item =TargetNode(XMaterial.MAP, "item")
        add("null", XMaterial.BOOK)
        add("task", XMaterial.REDSTONE_BLOCK)
        add("break block", XMaterial.STONE_PICKAXE, block, amount)
        add("place block", XMaterial.GRASS_BLOCK, block, amount)
        add("enchant item", XMaterial.ENCHANTING_TABLE,
            TargetNode(XMaterial.GOLD_INGOT, "cost", TargetNodeType.DOUBLE), number, item)
        add("player kill entity", XMaterial.GOLDEN_SWORD, number,
            TargetNode(XMaterial.SKELETON_SKULL, "entity", TargetNodeType.STRING),
            TargetNode(XMaterial.FLINT_AND_STEEL, "check", TargetNodeType.INT),
            TargetNode(XMaterial.WRITABLE_BOOK, "condition", TargetNodeType.LIST))
        add("left npc", XMaterial.PLAYER_HEAD, npcId, need)
        add("right npc", XMaterial.ZOMBIE_HEAD, npcId, need)
        add("left ady", XMaterial.SKELETON_SKULL, adyId, need)
        add("right ady", XMaterial.CREEPER_HEAD, adyId, need)
        add("player kill mythicmobs", XMaterial.WITHER_SKELETON_SKULL, mobs, number)
        add("player chat", XMaterial.PAPER, TargetNode(XMaterial.WHITE_DYE, "message", TargetNodeType.STRING), number)
        add("player send command", XMaterial.COMMAND_BLOCK,
            TargetNode(XMaterial.WHITE_DYE, "content", TargetNodeType.STRING), number)
        add("player death", XMaterial.LEATHER_HELMET, CauseNode(), number)
        add("player join server", XMaterial.WATER_BUCKET, number)
        add("player quit server", XMaterial.LAVA_BUCKET, number)
        add("player respawn", XMaterial.BEACON, number, need)
        add("player fish", XMaterial.FISHING_ROD,
            TargetNode(XMaterial.CARROT, "entitylist"),
            TargetNode(XMaterial.WOODEN_AXE, "hook"),
            TargetNode(XMaterial.NAME_TAG, "state"), TargetNode(XMaterial.EXPERIENCE_BOTTLE, "exp", TargetNodeType.INT), amount)
        add("craft item", XMaterial.CRAFTING_TABLE, item, amount,
            TargetNode(XMaterial.BOOK, "matrix"))
        val dialog = TargetNode(XMaterial.CARROT, "dialog", TargetNodeType.LIST)
        val reply = TargetNode(XMaterial.POTATO, "reply", TargetNodeType.LIST)
        add("player dialog", XMaterial.MAP, dialog, number)
        add("player reply", XMaterial.COMPASS, dialog, reply, number)
    }

    fun add(name: String, material: XMaterial = XMaterial.LIME_WOOL, vararg targetNode: TargetNode) {
        saveTarget.add(TargetStorage(name, material, *targetNode.toMutableList()))
    }

    fun getNodeList(name: String): MutableList<TargetNode> {
        return saveTarget.find { it.name == name }?.nodes?: mutableListOf()
    }

    fun getNode(name: String, node: String): TargetNode? {
        return getNodeList(name).find { it.node == node }
    }

    val saveTarget: MutableList<TargetStorage> = mutableListOf()

}

class TargetStorage(val name: String, val material: XMaterial, val nodes: MutableList<TargetNode> = mutableListOf()) {

    private fun spStr(): String = "TARGET_SELECT_${name.replace(" ", "_").uppercase()}"

    fun lang(player: Player) = player.asLangText(spStr(), name)

}

enum class TargetNodeType {
    STRING,INT,DOUBLE,BOOLEAN,LIST
}

open class TargetNode(val material: XMaterial, val node: String, val nodeType: TargetNodeType = TargetNodeType.LIST) {

    open fun contains(content: String, player: Player): Boolean {
        return runEval(player, content)
    }

}