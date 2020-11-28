package cn.inrhor.questengine.common.dialog

class DialogFile(
    val dialogID: String,
    val target: String,
    val condition: MutableList<String>,

    val ownLocation: String,
    val ownTextAddLocation: String,
    val ownTextContent: String,
    val ownItemAddLocation: String,
    val ownItemContent: String
) {
}