package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.database.Database

enum class DatabaseType {
    ERROR, LOCAL, MYSQL
}

object DatabaseManager {

    var type = DatabaseType.LOCAL

    fun init() {
        type = try {
            DatabaseType.valueOf(QuestEngine.config.getString("data.type")!!.uppercase())
        }catch (e: Exception) {
            DatabaseType.ERROR
        }
        Database.initDatabase()
    }

}