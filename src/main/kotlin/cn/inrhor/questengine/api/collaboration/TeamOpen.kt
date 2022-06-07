package cn.inrhor.questengine.api.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import org.bukkit.Bukkit
import org.bukkit.entity.Player
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
            val pData = it.getPlayerData()
            pData.teamData = null
        }
        TeamManager.teamsMap.remove(teamName)
        members.clear()
    }

    open fun getAmount(): Int = members.size

    /**
     * @param containLeader 是否包含队长
     */
    open fun playerMembers(containLeader: Boolean = true): MutableSet<Player> {
        val p = mutableSetOf<Player>()
        for (i in members) {
            if (!containLeader && i == leader) continue
            val m = Bukkit.getPlayer(i)
            if (m != null) {
                p.add(m)
            }
        }
        return p
    }

}