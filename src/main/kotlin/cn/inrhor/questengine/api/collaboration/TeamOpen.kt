package cn.inrhor.questengine.api.collaboration

import java.util.*

abstract class TeamOpen {

    abstract val teamUUID: UUID

    abstract var teamName: String

    abstract var leader: UUID

    abstract var members: MutableSet<UUID>

    open fun rename(newTeamName: String) {
        teamName = newTeamName
    }

    open fun addMember(member: UUID) {
        members.add(member)
    }

    open fun getAmount(): Int = members.size

}