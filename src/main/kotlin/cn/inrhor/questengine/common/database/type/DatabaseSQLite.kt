package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.manager.DataManager.navData
import cn.inrhor.questengine.api.manager.DataManager.storage
import cn.inrhor.questengine.api.manager.DataManager.tagsData
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.database.data.TrackData
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.nav.NavData
import cn.inrhor.questengine.common.quest.enum.StateType
import org.bukkit.entity.Player
import taboolib.common.platform.function.getDataFolder
import taboolib.common.util.Location
import taboolib.module.database.ColumnTypeSQLite
import taboolib.module.database.Table
import taboolib.module.database.getHost
import taboolib.platform.util.toBukkitLocation
import java.io.File
import java.util.*
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

    private val tableNav = Table("${table}_nav", host) {
        add("user") {
            type(ColumnTypeSQLite.TEXT, 36)
        }
        add("nav") {
            type(ColumnTypeSQLite.TEXT, 36)
        }
        add("state") {
            type(ColumnTypeSQLite.INTEGER, 16)
        }
        add("x") {
            type(ColumnTypeSQLite.NUMERIC, 16)
        }
        add("y") {
            type(ColumnTypeSQLite.NUMERIC, 16)
        }
        add("z") {
            type(ColumnTypeSQLite.NUMERIC, 16)
        }
        add("world") {
            type(ColumnTypeSQLite.TEXT, 64)
        }
        primaryKeyForLegacy += arrayOf("user", "nav")
    }

    /**
     * 扩展性数据
     */
    private val tableData = Table("${table}_data", host) {
        add("user") {
            type(ColumnTypeSQLite.TEXT, 36)
        }
        add("key") {
            type(ColumnTypeSQLite.TEXT, 64)
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
        tableNav.createTable(dataSource)
        tableData.createTable(dataSource)
    }

    override fun createQuest(uuid: UUID, questData: QuestData) {
        tableQuest.insert(dataSource) {
            value(
                uuid.toString(),
                questData.id,
                questData.state.int,
                questData.time,
                questData.end)
        }
    }

    override fun removeQuest(uuid: UUID, questID: String) {
        tableTarget.delete(dataSource) {
            where {
                "user" eq uuid.toString()
                "quest" eq questID
            }
        }
        tableQuest.delete(dataSource) {
            where {
                "user" eq uuid.toString()
                "quest" eq questID
            }
        }
    }

    override fun createTarget(uuid: UUID, targetData: TargetData) {
        tableTarget.insert(dataSource) {
            value(
                uuid.toString(),
                targetData.questID,
                targetData.id,
                targetData.schedule,
                targetData.state.int
            )
        }
    }

    override fun updateQuest(uuid: UUID, questID: String, key: String, value: Any) {
        tableQuest.update(dataSource) {
            where {
                "user" eq uuid.toString()
                "quest" eq questID
            }
            set(key, value)
        }
    }

    override fun updateTarget(uuid: UUID, target: TargetData, key: String, value: Any) {
        tableTarget.update(dataSource) {
            where {
                "user" eq uuid.toString()
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
            player.tagsData().tags.add(getString("tag"))
        }
        tableStorage.select(dataSource) {
            where {
                "user" eq uId
            }
        }.map {
            player.storage()[getString("key")] = getString("value")
        }
        tableNav.select(dataSource) {
            where {
                "user" eq uId
            }
        }.map {
            val navData = NavData(
                getString("nav"),
                Location(
                    getString("world"),
                    getDouble("x"),
                    getDouble("y"),
                    getDouble("z")
                ).toBukkitLocation(),
                NavData.State.fromInt(getInt("state"))
            )
            player.navData().add(navData)
        }
    }

    override fun addTag(uuid: UUID, tag: String) {
        tableTags.insert(dataSource) {
            value(uuid.toString(), tag)
        }
    }

    override fun removeTag(uuid: UUID, tag: String) {
        tableTags.delete(dataSource) {
            where {
                "user" eq uuid.toString()
                "tag" eq tag
            }
        }
    }

    override fun clearTag(uuid: UUID) {
        tableTags.delete(dataSource) {
            where {
                "user" eq uuid.toString()
            }
        }
    }

    override fun setStorage(uuid: UUID, key: String, value: Any) {
        // 查看是否存在，不存在则创建
        val uid = uuid.toString()
        if (tableStorage.find(dataSource) {
                where {
                    "user" eq uid
                    "key" eq key
                }
            }) {
            tableStorage.update(dataSource) {
                where {
                    "user" eq uid
                    "key" eq key
                }
                set("value", value)
            }
        }else {
            tableStorage.insert(dataSource) {
                value(uid, key, value)
            }
        }
    }

    override fun removeStorage(uuid: UUID, key: String) {
        tableStorage.delete(dataSource) {
            where {
                "user" eq uuid.toString()
                "key" eq key
            }
        }
    }

    override fun createNavigation(uuid: UUID, navId: String, navData: NavData) {
        tableNav.insert(dataSource) {
            val loc = navData.location
            value(
                uuid.toString(),
                navId,
                navData.state.int,
                loc.x,
                loc.y,
                loc.z,
                loc.world?.name?: "world"
            )
        }
    }

    override fun setNavigation(uuid: UUID, navId: String, key: String, value: Any) {
        tableNav.update(dataSource) {
            where {
                "user" eq uuid.toString()
                "nav" eq navId
            }
            set(key, value)
        }
    }

    override fun removeNavigation(uuid: UUID, navId: String) {
        tableNav.delete(dataSource) {
            where {
                "user" eq uuid.toString()
                "nav" eq navId
            }
        }
    }

    private fun setExtendData(uuid: UUID, key: String, value: String) {
        val uid = uuid.toString()
        if (tableData.find(dataSource) {
                where {
                    "user" eq uid
                    "key" eq key
                }
            }) {
            tableData.update(dataSource) {
                where {
                    "user" eq uid
                    "key" eq key
                }
                set("value", value)
            }
        }else {
            tableData.insert(dataSource) {
                value(uid, key, value)
            }
        }
    }

    override fun setTrack(uuid: UUID, trackData: TrackData) {
        setExtendData(uuid, "track_quest", trackData.questID)
        setExtendData(uuid, "track_target", trackData.targetID)
    }

    override fun removeTrack(uuid: UUID) {
        tableData.delete(dataSource) {
            where {
                "user" eq uuid.toString()
                "key" eq "track_quest"
            }
        }
        tableData.delete(dataSource) {
            where {
                "user" eq uuid.toString()
                "key" eq "track_target"
            }
        }
    }

    override fun migrate(type: DatabaseType) {
        if (type == DatabaseType.LOCAL) {
            // 遍历存储
            tableQuest.select(dataSource) {

            }.map {
                val uuid = UUID.fromString(getString("user"))
                val questData = QuestData(
                    id = getString("quest"),
                    state = StateType.fromInt(getInt("state")),
                    time = getString("time"),
                    end = getString("end")
                )
                database.createQuest(uuid, questData)
                val questId = questData.id
                tableTarget.select(dataSource) {
                    where {
                        "user" eq uuid.toString()
                        "quest" eq questId
                    }
                }.map {
                    val targetData = TargetData(
                        id = getString("target"),
                        questID = questId,
                        schedule = getInt("schedule"),
                        state = StateType.fromInt(getInt("state"))
                    )
                    database.createTarget(uuid, targetData)
                }
            }
            tableTags.select(dataSource) {

            }.map {
                val uuid = UUID.fromString(getString("user"))
                database.addTag(uuid, getString("tag"))
            }
            tableStorage.select(dataSource) {
            }.map {
                val uuid = UUID.fromString(getString("user"))
                database.setStorage(uuid, getString("key"), getString("value"))
            }
            tableNav.select(dataSource) {
            }.map {
                val uuid = UUID.fromString(getString("user"))
                val navData = NavData(
                    getString("nav"),
                    Location(
                        getString("world"),
                        getDouble("x"),
                        getDouble("y"),
                        getDouble("z")
                    ).toBukkitLocation(),
                    NavData.State.fromInt(getInt("state"))
                )
                database.createNavigation(uuid, getString("nav"), navData)
            }
            tableData.select(dataSource) {
                where {
                    "key" eq "track_quest"
                }
            }.map {
                val uuid = UUID.fromString(getString("user"))
                val questId = getString("value")
                tableData.select(dataSource) {
                    where {
                        "user" eq uuid.toString()
                        "key" eq "track_target"
                    }
                }.map {
                    val targetId = getString("value")
                    database.setTrack(uuid, TrackData(questId, targetId))
                }
            }
        }
    }

}