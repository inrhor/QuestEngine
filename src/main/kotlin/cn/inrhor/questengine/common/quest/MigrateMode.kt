package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.manager.QuestManager.saveFile
import cn.inrhor.questengine.common.quest.target.node.ObjectiveNode
import cn.inrhor.questengine.server.PluginLoader
import taboolib.common.platform.function.info
import taboolib.common.platform.function.warning

/**
 * 迁移模式
 *
 * 1.0
 *
 * 本次迁移任务：目标条目的变化
 */
class MigrateMode {

    fun task() {
        info("[ 迁移 ] 开始迁移任务")

        // 遍历所有任务的目标
        QuestManager.getQuestMap().values.forEach { q ->
            q.target.forEach { t ->
                val map = t.nodeMap
                val pass = t.pass
                when (t.event) {
                    "break block", "plack block" -> {
                        pass.material = listMigrate(map, "material")
                    }
                    "craft item" -> {
                        pass.item = listMigrate(map, "item")
                    }
                    "enchant item" -> {
                        pass.item = listMigrate(map, "item")
                    }
                    "player kill entity" -> {
                        warning("[ 迁移 ] 'player kill entity' 不支持迁移，请手动迁移")
                        warning("[ 迁移 ] 目录：${q.path}")
                    }
                    "player send command" -> {
                        pass.content = listMigrate(map, "content")
                    }
                    "player chat" -> {
                        pass.content = listMigrate(map, "message")
                    }
                    "player death" -> {
                        pass.cause = listMigrate(map, "cause")
                    }
                    "player fish" -> {
                        pass.entityTypes = listMigrate(map, "entitylist")
                        pass.hook = listMigrate(map, "hook")
                        pass.exp = map["exp"]?.get(0)?.toInt()?: 0
                        pass.state = listMigrate(map, "state")
                    }
                    "left ady", "right ady", "left npc", "right npc" -> {
                        pass.id = listMigrate(map, "id")
                        pass.need = listMigrate(map, "need")
                    }
                    "join csg", "quit csg" -> {
                        pass.id = listMigrate(map, "room")
                    }
                    "player dialog" -> {
                        pass.dialog = listMigrate(map, "dialog")
                    }
                    "player reply" -> {
                        pass.dialog = listMigrate(map, "dialog")
                        pass.reply = listMigrate(map, "reply")
                    }
                    "player kill mythicmobs" -> {
                        pass.id = listMigrate(map, "mobs")
                    }
                }
                amountMigrate(map, pass)
            }
            q.saveFile()
        }
        PluginLoader.unloadTask()
        PluginLoader.loadTask()
        info("[ 迁移 ] 迁移任务已完成，任务配置已重载")
    }

    private fun amountMigrate(map: MutableMap<String, MutableList<String>>, pass: ObjectiveNode) {
        pass.amount = map["amount"]?.get(0)?.toInt()?: map["number"]?.get(0)?.toInt()?: 0
    }

    private fun listMigrate(map: MutableMap<String, MutableList<String>>, meta: String): List<String> {
        return map[meta]?: mutableListOf()
    }

}