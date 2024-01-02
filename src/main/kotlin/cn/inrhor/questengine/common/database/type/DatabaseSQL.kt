package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.manager.DataManager.navData
import cn.inrhor.questengine.api.manager.DataManager.storage
import cn.inrhor.questengine.api.manager.DataManager.tagsData
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.database.data.TrackData
import cn.inrhor.questengine.common.database.data.quest.*
import cn.inrhor.questengine.common.nav.NavData
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.utlis.time.toDate
import cn.inrhor.questengine.utlis.time.toStr
import org.bukkit.entity.Player
import taboolib.common.util.Location
import taboolib.module.database.ColumnOptionSQL
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.HostSQL
import taboolib.module.database.Table
import taboolib.platform.util.toBukkitLocation
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.sql.DataSource


class DatabaseSQL: Database() {

    private val host = HostSQL(QuestEngine.config.getConfigurationSection("data.mysql")!!)

    private val table = QuestEngine.config.getString("data.mysql.table")

    private val tableUser = Table(table + "_user", host) {
        add { id() }
        add("uuid") {
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.UNIQUE_KEY)
            }
        }
    }

    private val tableQuest = Table(table + "_quest", host) {
        add { id() }
        add("user") {
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("quest") { // questID
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("state") {
            type(ColumnTypeSQL.INT, 16)
        }
        add("time") {
            type(ColumnTypeSQL.DATETIME)
        }
        add("end") {
            type(ColumnTypeSQL.DATETIME) {
                def()
            }
        }
    }

    private val tableTarget = Table(table + "_target", host) {
        add("quest") { // mysql quest id
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("id") {// target id
            type(ColumnTypeSQL.VARCHAR, 64) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("schedule") {
            type(ColumnTypeSQL.INT)
        }
        add("state") {
            type(ColumnTypeSQL.INT, 16)
        }
    }

    private val tableTags = Table(table + "_tags", host) {
        add("user") {
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("tag") {
            type(ColumnTypeSQL.VARCHAR, 64)
        }
    }

    private val tableStorage = Table(table + "_storage", host) {
        add("user") {
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("key") {
            type(ColumnTypeSQL.VARCHAR, 64)
        }
        add("value") {
            type(ColumnTypeSQL.VARCHAR, 64)
        }
    }

    private val tableNavigation = Table(table + "_navigation", host) {
        add("user") {
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("nav") {
            type(ColumnTypeSQL.VARCHAR, 64) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("x") {
            type(ColumnTypeSQL.DOUBLE, 16)
        }
        add("y") {
            type(ColumnTypeSQL.DOUBLE, 16)
        }
        add("z") {
            type(ColumnTypeSQL.DOUBLE, 16)
        }
        add("world") {
            type(ColumnTypeSQL.VARCHAR, 64)
        }
        add("state") {
            type(ColumnTypeSQL.INT, 16)
        }
    }

    private val tableData = Table(table + "_data", host) {
        add("user") {
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("key") {
            type(ColumnTypeSQL.VARCHAR, 64) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("value") {
            type(ColumnTypeSQL.VARCHAR, 64)
        }
    }

    private val source: DataSource by lazy {
        host.createDataSource()
    }

    init {
        tableUser.createTable(source)
        tableQuest.createTable(source)
        tableTarget.createTable(source)
        tableTags.createTable(source)
        tableStorage.createTable(source)
        tableNavigation.createTable(source)
        tableData.createTable(source)
    }

    private fun userId(uuid: UUID): Long {
        if (saveUserId.contains(uuid)) return saveUserId[uuid]!!
        val uId = tableUser.select(source) {
            rows("id")
            where { "uuid" eq uuid.toString() }
        }.map {
            getLong("id")
        }.firstOrNull() ?: -1L
        saveUserId[uuid] = uId
        return uId
    }

    override fun pull(player: Player) {
        val uuid = player.uniqueId
        val pData = uuid.getPlayerData()
        if (!tableUser.find(source) { where { "uuid" eq uuid.toString() } }) {
            tableUser.insert(source, "uuid") {
                value(uuid.toString())
            }
        }
        val uId = userId(uuid)
        tableQuest.select(source) {
            rows("id", "quest", "state", "time", "end")
            where { "user" eq uId }
        }.map {
            val questData = QuestData(
                id = getString("quest"),
                state = StateType.fromInt(getInt("state")),
                time = getDate("time").toStr(),
                end = getDate("end").toStr()
            )
            val questId = questData.id
            tableTarget.select(source) {
                rows("id", "schedule", "state")
                where { "quest" eq questId }
            }.map {
                val targetData = TargetData(
                    id = getString("id"),
                    questID = questId,
                    schedule = getInt("schedule"),
                    state = StateType.fromInt(getInt("state"))
                )
                questData.target.add(targetData)
            }
            questData.updateTime(player)
            pData.dataContainer.quest[questId] = questData
        }
        tableTags.select(source) {
            where { "user" eq uId }
            rows("tag")
        }.map {
            player.tagsData().tags.add(getString("tag"))
        }
        tableStorage.select(source) {
            where { "user" eq uId }
            rows("key", "value")
        }.map {
            player.storage()[getString("key")] = getString("value")
        }
        tableNavigation.select(source) {
            where { "user" eq uId }
            rows("nav", "x", "y", "z", "world", "state")
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

    override fun createQuest(uuid: UUID, questData: QuestData) {
        val questID = questData.id
        val state = questData.state.int
        val uId = userId(uuid)
        tableQuest.insert(source, "user", "quest", "state", "time") {
            value(uId, questID, state, questData.time.toDate())
        }
    }

    override fun removeQuest(uuid: UUID, questID: String) {
        val uId = userId(uuid)
        tableTarget.delete(source) {
            where {
                and {
                    "user" eq uId
                    "quest" eq questID
                }
            }
        }
        tableQuest.delete(source) {
            where {
                and {
                    "user" eq uId
                    "quest" eq questID
                }
            }
        }
    }

    override fun createTarget(uuid: UUID, targetData: TargetData) {
        val uId = userId(uuid)
        val qId = findQuest(uId, targetData.questID)
        tableTarget.insert(source, "user", "quest", "id", "schedule", "state") {
            value(uId, qId, targetData.id, targetData.schedule, targetData.state.int)
        }
    }


    private fun findQuest(uId: Long, questID: String): Long {
        return tableQuest.select(source) {
            where { "user" eq uId and("quest" eq questID) }
        }.firstOrNull { getLong("id") } ?: -1L
    }

    override fun updateQuest(uuid: UUID, questID: String, key: String, value: Any) {
        val uId = userId(uuid)
        tableQuest.update(source) {
            where {
                and {
                    "user" eq uId
                    "quest" eq questID
                }
            }
            set(key, value)
        }
    }

    override fun updateTarget(uuid: UUID, target: TargetData, key: String, value: Any) {
        val uId = userId(uuid)
        val qId = findQuest(uId, target.questID)
        tableTarget.update(source) {
            where {
                and {
                    "user" eq uId
                    "quest" eq qId
                    "id" eq target.id
                }
            }
            set(key, value)
        }
    }

    override fun addTag(uuid: UUID, tag: String) {
        val uId = userId(uuid)
        tableTags.insert(source, "user", "tag") {
            value(uId, tag)
        }
    }

    override fun removeTag(uuid: UUID, tag: String) {
        val uId = userId(uuid)
        tableTags.delete(source) {
            where {
                and {
                    "user" eq uId
                    "tag" eq tag
                }
            }
        }
    }

    override fun clearTag(uuid: UUID) {
        tableTags.delete(source) {
            where {
                "user" eq uuid.toString()
            }
        }
    }

    override fun setStorage(uuid: UUID, key: String, value: Any) {
        val uId = userId(uuid)
        if (tableStorage.find(source) {
                where {
                    and {
                        "user" eq uId
                        "key" eq key
                    }
                }
            }) {
            tableStorage.update(source) {
                where {
                    and {
                        "user" eq uId
                        "key" eq key
                    }
                }
                set("value", value)
            }
        }else {
            tableStorage.insert(source, "user", "key", "value") {
                value(uId, key, value)
            }
        }
    }

    override fun removeStorage(uuid: UUID, key: String) {
        val uId = userId(uuid)
        tableStorage.delete(source) {
            where {
                and {
                    "user" eq uId
                    "key" eq key
                }
            }
        }
    }

    override fun createNavigation(uuid: UUID, navId: String, navData: NavData) {
        val uId = userId(uuid)
        tableNavigation.insert(source, "user", "nav", "x", "y", "z", "world", "state") {
            val loc = navData.location
            value(
                uId,
                navId,
                loc.x,
                loc.y,
                loc.z,
                loc.world?.name?: "world",
                navData.state.int)
        }
    }

    override fun setNavigation(uuid: UUID, navId: String, key: String, value: Any) {
        val uId = userId(uuid)
        tableNavigation.update(source) {
            where {
                and {
                    "user" eq uId
                    "nav" eq navId
                }
            }
            set(key, value)
        }
    }

    override fun removeNavigation(uuid: UUID, navId: String) {
        val uId = userId(uuid)
        tableNavigation.delete(source) {
            where {
                and {
                    "user" eq uId
                    "nav" eq navId
                }
            }
        }
    }

    /**
     * 设置扩展
     */
    private fun setExtendData(uuid: UUID, key: String, value: String) {
        val uId = userId(uuid)
        if (tableData.find(source) {
                where {
                    and {
                        "user" eq uId
                        "key" eq key
                    }
                }
            }) {
            tableData.update(source) {
                where {
                    and {
                        "user" eq uId
                        "key" eq key
                    }
                }
                set("value", value)
            }
        }else {
            tableData.insert(source, "user", "key", "value") {
                value(uId, key, value)
            }
        }
    }

    override fun setTrack(uuid: UUID, trackData: TrackData) {
        setExtendData(uuid, "track_quest", trackData.questID)
        setExtendData(uuid, "track_target", trackData.targetID)
    }

    override fun removeTrack(uuid: UUID) {
        val uId = userId(uuid)
        tableData.delete(source) {
            where {
                and {
                    "user" eq uId
                    "key" eq "track_quest"
                }
            }
        }
        tableData.delete(source) {
            where {
                and {
                    "user" eq uId
                    "key" eq "track_target"
                }
            }
        }
    }

    override fun migrate(type: DatabaseType) {
        if (type == DatabaseType.MYSQL) {
            tableUser.select(source) {
                rows("uuid")
            }.map {
                val uuid = UUID.fromString(getString("uuid"))
                val uId = userId(uuid)
                tableQuest.select(source) {
                    where { "user" eq uId }
                }.map {
                    val questData = QuestData(
                        id = getString("quest"),
                        state = StateType.fromInt(getInt("state")),
                        time = getDate("time").toStr(),
                        end = getDate("end").toStr()
                    )
                    database.createQuest(uuid, questData)
                    val questId = questData.id
                    tableTarget.select(source) {
                        where { "quest" eq questId }
                    }.map {
                        val targetData = TargetData(
                            id = getString("id"),
                            questID = questId,
                            schedule = getInt("schedule"),
                            state = StateType.fromInt(getInt("state"))
                        )
                        database.createTarget(uuid, targetData)
                    }
                }
                tableTags.select(source) {
                    where { "user" eq uId }
                }.map {
                    database.addTag(uuid, getString("tag"))
                }
                tableStorage.select(source) {
                    where { "user" eq uId }
                }.map {
                    database.setStorage(uuid, getString("key"), getString("value"))
                }
                tableNavigation.select(source) {
                    where { "user" eq uId }
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
                    database.createNavigation(uuid, getString("nav"), navData)
                }
                tableData.select(source) {
                    where {
                        "user" eq uId
                        "key" eq "track_quest"
                    }
                }.map {
                    val questID = getString("value")
                    tableData.select(source) {
                        where {
                            and {
                                "user" eq uId
                                "key" eq "track_target"
                            }
                        }
                    }.map {
                        val targetID = getString("value")
                        database.setTrack(uuid, TrackData(questID, targetID))
                    }
                }
            }
        }
    }

    companion object {
        private val saveUserId = ConcurrentHashMap<UUID, Long>()
    }

}