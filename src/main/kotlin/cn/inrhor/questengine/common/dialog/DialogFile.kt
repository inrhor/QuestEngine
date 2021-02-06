package cn.inrhor.questengine.common.dialog

import cn.inrhor.questengine.common.dialog.animation.Text
import cn.inrhor.questengine.common.dialog.animation.TextAnimation
import org.bukkit.configuration.ConfigurationSection

class DialogFile {

    var dialogID: String? = null
    var target: String? = null
    var condition: MutableList<String>? = null

    var ownLocation: String? = null

    var ownTextAddLocation: String? = null
    var ownTextContent: MutableList<String>? = null

    var ownItemAddLocation: String? = null
    var ownItemContent: MutableList<String>? = null

    private var ownTextAnimation: TextAnimation? = null

    var frame: Int = -1

    fun init(config: ConfigurationSection) {
        if (!config.contains("target")) {
            return
        }
        if (!config.contains("condition")) {
            return
        }
        val ownSec = "dialog.own."
        if (!config.contains(ownSec+"location")) {
            return
        }
        if (!config.contains(ownSec+"text.addLocation")) {
            return
        }
        if (!config.contains(ownSec+"text.content")) {
            return
        }
        if (!config.contains(ownSec+"item.addLocation")) {
            return
        }
        if (!config.contains(ownSec+"item.content")) {
            return
        }
        this.dialogID = config.name
        this.target = config.getString("target")
        this.condition = config.getStringList("condition")
        this.ownLocation = config.getString(ownSec+"location")
        this.ownTextAddLocation = config.getString(ownSec+"text.addLocation")
        this.ownTextContent = config.getStringList(ownSec+"text.content")
        this.ownItemAddLocation = config.getString(ownSec+"item.addLocation")
        this.ownItemContent = config.getStringList(ownSec+"item.content")

        this.frame = config.getInt(ownSec+"frame")

        DialogManager().register(this.dialogID!!, this)
        animation()
    }

    /**
     * 处理动画到表中
     */
    fun animation() {
        val textAnimation =
            TextAnimation(ownTextContent!!)
        textAnimation.init()
        ownTextAnimation = textAnimation
    }

    /**
     * 主体文字组中这一行包含的标签内容
     */
    fun getOwnTheLineList(line: Int): MutableList<Text> {
        return ownTextAnimation!!.getTextContent(line)
    }

}