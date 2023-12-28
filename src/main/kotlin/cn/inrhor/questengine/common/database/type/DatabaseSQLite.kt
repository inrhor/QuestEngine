package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.manager.DataManager.setStorage
import cn.inrhor.questengine.api.manager.TagsManager.addTag
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.enum.StateType
import org.bukkit.entity.Player
import taboolib.common.platform.function.getDataFolder
import taboolib.module.database.ColumnTypeSQLite
import taboolib.module.database.Table
import taboolib.module.database.getHost
import java.io.File
import javax.sql.DataSource

class DatabaseSQLite: Database() {

    private val host = File(getDataFolder(), "data.db").getHost()

    private val table = QuestEngine.config.getString("data.sql.table")

    private val tableQuest = Table("${table}_quest", host) {
        add("user") { // player uuid
            type(ColumnTypeSQLite.TEXT, 36)
        }
        add("quest") { // questID
            type(ColumnTypeSQLite.TEXT, 36)
        }
        add("state") {
            type(ColumnTypeSQLite.INTEGER, 16)
        }
        add("time") {
            type(ColumnTypeSQLite.TEXT)
        }
        add("end") {
            type(ColumnTypeSQLite.TEXT)
        }
        primaryKeyForLegacy += arrayOf("user", "quest")
    }

    private val tableTarget = Table("${table}_target", host) {
        add("user") {
            type(ColumnTypeSQLite.TEXT, 36)
        }
        add("quest") {
            type(ColumnTypeSQLite.TEXT, 36)
        }
        add("target") {
            type(ColumnTypeSQLite.TEXT, 36)
        }
        add("schedule") {
            type(ColumnTypeSQLite.INTEGER, 16)
        }
        add("state") {
            type(ColumnTypeSQLite.INTEGER, 16)
        }
        primaryKeyForLegacy += arrayOf("user", "quest", "target")
    }

    private val tableTags = Table("${table}_tags", host) {
        add("user") {
            type(ColumnTypeSQLite.TEXT, 36)
        }
        add("tag") {
            type(ColumnTypeSQLite.TEXT, 64)
        }
        primaryKeyForLegacy += arrayOf("user", "tag")
    }

    private val tableStorage = Table("${table}_storage", host) {
        add("user") {
            type(ColumnTypeSQLite.TEXT, 36)
        }
        add("key") {
            type(ColumnTypeSQLite.TEXT, 36)
        }
        add("value") {
            type(ColumnTypeSQLite.TEXT, 64)
        }
        primaryKeyForLegacy += arrayOf("user", "key")
    }

    private val dataSource: DataSource by lazy {
        host.createDataSource()
    }

    init {
        tableQuest.createTable(dataSource)
        tableTarget.createTable(dataSource)
        tableTags.createTable(dataSource)
        tableStorage.createTable(dataSource)
    }

    override fun createQuest(player: Player, questData: QuestData) {
        tableQuest.insert(dataSource) {
            value(
                player.uniqueId.toString(),
                questData.id,
                questData.state.int,
                questData.time,
                questData.end)
        }
    }

    override fun removeQuest(player: Player, questID: String) {
        tableTarget.delete(dataSource) {
            where {
                "user" eq player.uniqueId.toString()
                "quest" eq questID
            }
        }
        tableQuest.delete(dataSource) {
            where {
                "user" eq player.uniqueId.toString()
                "quest" eq questID
            }
        }
    }

    override fun createTarget(player: Player, targetData: TargetData) {
        tableTarget.insert(dataSource) {
            value(
                player.uniqueId.toString(),
                targetData.questID,
                targetData.id,
                targetData.schedule,
                targetData.state.int
            )
        }
    }

    override fun updateQuest(player: Player, questID: String, key: String, value: Any) {
        tableQuest.update(dataSource) {
            where {
                "user" eq player.uniqueId.toString()
                "quest" eq questID
            }
            set(key, value)
        }
    }

    override fun updateTarget(player: Player, target: TargetData, key: String, value: Any) {
        tableTarget.update(dataSource) {
            where {
                "user" eq player.uniqueId.toString()
                "quest" eq target.questID
                "target" eq target.id
            }
            set(key, value)
        }
    }

    override fun pull(player: Player) {
        val uuid = player.uniqueId
        val pData = uuid.getPlayerData()
        val uId = uuid.toString()
        tableQuest.select(dataSource) {
            where {
                "user" eq uId
            }
        }.map {
            val questData = QuestData(
                id = getString("quest"),
                state = StateType.fromInt(getInt("state")),
                time = getString("time"),
                end = getString("end")
            )
            val questId = questData.id
            tableTarget.select(dataSource) {
                where {
                    "user" eq uId
                    "quest" eq questId
                }
            }.map {
                val targetData = TargetData(
                    id = getString("target"),
                    questID = questId,
                    schedule = getInt("schedule"),
                    state = StateType.fromInt(getInt("state"))
                )
                questData.target.add(targetData)
            }
            questData.updateTime(player)
            pData.dataContainer.quest[questId] = questData
        }
        tableTags.select(dataSource) {
            where {
                "user" eq uId
            }
        }.map {
            player.addTag(getString("tag"))
        }
        tableStorage.select(dataSource) {
            where {
                "user" eq uId
            }
        }.map {
            player.setStorage(getString("key"), getString("value"))
        }
    }

    override fun addTag(player: Player, tag: String) {
        tableTags.insert(dataSource) {
            value(player.uniqueId.toString(), tag)
        }
    }

    override fun removeTag(player: Player, tag: String) {
        tableTags.delete(dataSource) {
            where {
                "user" eq player.uniqueId.toString()
                "tag" eq tag
            }
        }
    }

    override fun clearTag(player: Player) {
        tableTags.delete(dataSource) {
            where {
                "user" eq player.uniqueId.toString()
            }
        }
    }

    override fun addStorage(player: Player, key: String, value: Any) {
        tableStorage.insert(dataSource) {
            value(player.uniqueId.toString(), key, value)
        }
    }

    override fun removeStorage(player: Player, key: String) {
        tableStorage.delete(dataSource) {
            where {
                "user" eq player.uniqueId.toString()
                "key" eq key
            }
        }
    }

}