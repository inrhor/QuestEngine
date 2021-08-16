package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.database.Database

enum class DatabaseType {
    ERROR, LOCAL, MYSQL
}

object DatabaseManager {

    var type = DatabaseType.LOCAL

    fun init() {
        if (QuestEngine.config.getString("data.type")!!.uppercase() == "MYSQL") {
            type = DatabaseType.MYSQL
        }
        Database.initDatabase()
    }

}