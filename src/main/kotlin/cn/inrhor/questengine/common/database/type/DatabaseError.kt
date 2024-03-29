package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.TrackData
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.nav.NavData
import org.bukkit.entity.Player
import java.util.*

class DatabaseError(private val cause: Throwable): Database() {

    override fun pull(player: Player) {
        error()
    }

    override fun createQuest(uuid: UUID, questData: QuestData) {
        error()
    }

    override fun createTarget(uuid: UUID,targetData: TargetData) {
        error()
    }

    override fun removeQuest(uuid: UUID,questID: String) {
        error()
    }

    override fun updateQuest(uuid: UUID,questID: String, key: String, value: Any) {
        error()
    }

    override fun updateTarget(uuid: UUID,target: TargetData, key: String, value: Any) {
        error()
    }

    override fun addTag(uuid: UUID,tag: String) {
        error()
    }

    override fun removeTag(uuid: UUID,tag: String) {
        error()
    }

    override fun setStorage(uuid: UUID,key: String, value: Any) {
        error()
    }

    override fun removeStorage(uuid: UUID,key: String) {
        error()
    }

    override fun clearTag(uuid: UUID) {
        error()
    }

    override fun createNavigation(uuid: UUID,navId: String, navData: NavData) {
        error()
    }

    override fun setNavigation(uuid: UUID,navId: String, key: String, value: Any) {
        error()
    }

    override fun removeNavigation(uuid: UUID, navId: String) {
        error()
    }

    override fun removeTrack(uuid: UUID) {
        error()
    }

    override fun setTrack(uuid: UUID, trackData: TrackData) {
        error()
    }

    override fun migrate(type: DatabaseType) {
        error()
    }

    /**
     * 重复使用IllegalAccessError
     */
    private fun error() {
        throw IllegalAccessError("Database init failed: ${cause.localizedMessage}")
    }

}