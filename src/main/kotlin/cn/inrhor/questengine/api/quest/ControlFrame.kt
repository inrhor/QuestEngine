package cn.inrhor.questengine.api.quest

data class ControlFrame(
    var id: String = "", var queue: QueuePriority = QueuePriority.NORMAL,
    var type: QueueType = QueueType.ACCEPT,
    var select: SelectObject = SelectObject.SELF,
    var script: String = "")
