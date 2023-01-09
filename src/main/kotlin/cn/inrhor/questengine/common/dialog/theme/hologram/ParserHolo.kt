package cn.inrhor.questengine.common.dialog.theme.hologram

fun parserOrigin(origin: OriginLocation, content: String) {
    val u = content.lowercase()
    if (u.startsWith("initloc ")) {
        origin.reset(content)
    }else if (u.startsWith("addloc ")) {
        origin.add(content)
    }else if (u.startsWith("nexty ")) {
        origin.nextY = content.split(" ")[1].toDouble()
    }
}