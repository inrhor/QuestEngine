package cn.inrhor.questengine.api.collaboration

import java.util.*

class TeamData(
    override val teamUUID: UUID,
    override var teamName: String,
    override var leader: UUID,
    override var members: MutableSet<UUID>): TeamOpen() {

    constructor(teamName: String, leader: UUID):
            this(UUID.randomUUID(), teamName, leader, mutableSetOf(leader))

}
