package cn.inrhor.questengine.api.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.database.data.DataStorage
import java.util.*

abstract class TeamOpen {

    abstract val teamUUID: UUID

    abstract var teamName: String

    abstract var leader: UUID

    abstract var members: MutableSet<UUID>

    open var asks: MutableSet<UUID> = mutableSetOf()

    open fun rename(newTeamName: String) {
        teamName = newTeamName
    }

    open fun addMember(member: UUID) {
        members.add(member)
    }

    open fun delTeam() {
        members.forEach {
            val pData = DataStorage.getPlayerData(it)?: return@forEach
            pData.teamData = null
        }
        TeamManager.teamsMap.remove(teamName)
        members.clear()
    }

    open fun getAmount(): Int = members.size

}