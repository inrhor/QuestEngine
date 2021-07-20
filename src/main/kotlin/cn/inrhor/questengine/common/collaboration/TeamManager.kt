package cn.inrhor.questengine.common.collaboration

import cn.inrhor.questengine.api.collaboration.TeamData
import cn.inrhor.questengine.api.collaboration.TeamOpen
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.PlayerData
import java.util.*

object TeamManager {

    /*
     *  根据队伍名称对应队伍
     */
    val teamsMap = mutableMapOf<String, TeamOpen>()

    fun hasTeam(pUUID: UUID): Boolean {
        val pData = DataStorage.getPlayerData(pUUID)?: return false
        return hasTeam(pData)
    }

    fun hasTeam(pData: PlayerData): Boolean = (pData.teamData != null)

    fun isLeader(pUUID: UUID, teamName: String): Boolean {
        val teamData = getTeamData(teamName)?: return false
        return isLeader(pUUID, teamData)
    }

    fun isLeader(pUUID: UUID, teamData: TeamOpen): Boolean {
        return (teamData.leader == pUUID)
    }

    fun getTeamData(teamName: String): TeamOpen? = teamsMap[teamName]

    fun getTeamData(pUUID: UUID): TeamOpen? {
        val pData = DataStorage.getPlayerData(pUUID)?: return null
        return pData.teamData
    }

    fun getMemberAmount(teamName: String): Int {
        val teamData = getTeamData(teamName)?: return 0
        return teamData.getAmount()
    }

    fun getMemberAmount(teamData: TeamOpen): Int {
        return teamData.getAmount()
    }

    fun createTeam(teamName: String, leader: UUID) {
        val pData = DataStorage.getPlayerData(leader)?: return
        if (hasTeam(pData)) return
        if (teamsMap.containsKey(teamName)) return
        val teamData = TeamData(teamName, leader)
        pData.teamData = teamData
        teamsMap[teamName] = teamData
    }

    fun addMember(mUUID: UUID, teamData: TeamOpen) {
        if (teamData.members.contains(mUUID)) return
        teamData.members.add(mUUID)
        removeAsk(mUUID, teamData)
    }

    fun removeMember(mUUID: UUID, teamData: TeamOpen) {
        if (!teamData.members.contains(mUUID)) return
        teamData.members.remove(mUUID)
    }

    fun removeAsk(aUUID: UUID, teamData: TeamOpen) {
        teamData.asks.remove(aUUID)
    }

    fun addAsk(aUUID: UUID, teamData: TeamOpen) {
        teamData.asks.add(aUUID)
    }

}