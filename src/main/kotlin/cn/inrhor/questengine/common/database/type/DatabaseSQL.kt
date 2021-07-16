package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.database.Database
import io.izzel.taboolib.module.db.sql.*
import org.bukkit.entity.Player

class DatabaseSQL: Database() {

    val host = SQLHost(QuestEngine.config.getConfigurationSection("data.mysql"), QuestEngine.plugin, true)

    val table = QuestEngine.config.getString("data.mysql.table")

    val tableQuest = SQLTable(
        table+"_user_quest",
        SQLColumn.PRIMARY_KEY_ID,
        SQLColumnType.VARCHAR.toColumn("uuid").columnOptions(SQLColumnOption.UNIQUE_KEY),
        SQLColumnType.VARCHAR.toColumn("questID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.JSON.toColumn("finishedMainQuest")
    )

    val tableMainQuest = SQLTable(
        table+"_user_main_quest",
        SQLColumnType.VARCHAR.toColumn("uuid").columnOptions(SQLColumnOption.UNIQUE_KEY),
        SQLColumnType.VARCHAR.toColumn("mainQuestID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn("state"),
        SQLColumnType.JSON.toColumn("targets"),
        SQLColumnType.JSON.toColumn("rewards")
    )

    val tableSubQuest = SQLTable(
        table+"_user_sub_quest",
        SQLColumnType.VARCHAR.toColumn("uuid").columnOptions(SQLColumnOption.UNIQUE_KEY),
        SQLColumnType.VARCHAR.toColumn("mainQuestID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn("subQuestID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn("state"),
        SQLColumnType.JSON.toColumn("targets"),
        SQLColumnType.JSON.toColumn("rewards")
    )

    override fun pull(player: Player) {

    }

    override fun push(player: Player) {

    }


}