package cn.inrhor.questengine.api.quest

data class QuestFrame(
    var id: String = "unknownID", var name: String = "", var note: String ="",
    val accept: AcceptAddon = AcceptAddon(),
    val time: TimeAddon = TimeAddon(),
    val mode: ModeAddon = ModeAddon(),
    val group: GroupAddon = GroupAddon(),
    val target: MutableList<TargetFrame> = mutableListOf(),
    val control: MutableList<ControlFrame>
)