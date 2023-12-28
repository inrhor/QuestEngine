package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import org.bukkit.entity.Player

class DatabaseError(val cause: Throwable): Database() {

    override fun pull(player: Player) {
        error()
    }

    override fun createQuest(player: Player, questData: QuestData) {
        error()
    }

    override fun createTarget(player: Player, targetData: TargetData) {
        error()
    }

    override fun removeQuest(player: Player, questID: String) {
        error()
    }

    override fun updateQuest(player: Player, questID: String, key: String, value: Any) {
        error()
    }

    override fun updateTarget(player: Player, target: TargetData, key: String, value: Any) {
        error()
    }

    override fun addTag(player: Player, tag: String) {
        error()
    }

    override fun removeTag(player: Player, tag: String) {
        error()
    }

    override fun setStorage(player: Player, key: String, value: Any) {
        error()
    }

    override fun removeStorage(player: Player, key: String) {
        error()
    }

    override fun clearTag(player: Player) {
        error()
    }

    /**
     * 重复使用IllegalAccessError
     */
    private fun error() {
        throw IllegalAccessError("Database init failed: ${cause.localizedMessage}")
    }

}