package cn.inrhor.questengine.utlis.ui

import cn.inrhor.questengine.utlis.toJsonStr
import taboolib.common.platform.function.info
import taboolib.library.configuration.YamlConfiguration
import taboolib.module.chat.TellrawJson

/**
 * 高度自定义 JSON 内容
 *
 * 窗口组件
 */
open class BuilderJsonUI {

    /**
     * 内容物
     */
    val description = mutableListOf<String>()

    /**
     * 文字组件，使内容物调用指定组件
     */
    val textList = mutableMapOf<String, TellrawJson>()

    fun yamlAddDesc(yaml: YamlConfiguration, node: String) {
        yaml.getStringList(node).forEach {
            description.add(it)
        }
    }

    fun sectionAdd(yaml: YamlConfiguration, path: String) {
        yaml.getConfigurationSection(path).getKeys(false).forEach { sort ->
            yaml.getConfigurationSection("$path.$sort").getKeys(false).forEach { sign ->
                val id = "$sort.$sign"
                val node = "$path.$id"
                if (sign == "note") {
                    yaml.getStringList(node).forEach { n ->
                        description.add(n)
                    }
                }else {
                    val text = textComponent {
                        text = yaml.getStringList("$node.text")
                        hover = yaml.getStringList("$node.hover")
                        command = "/qen handbook sort $sort"
                    }
                    textList[id] = text
                }
            }
        }
    }

    open fun build(): String {
        var text = description.toJsonStr()
        var json = TellrawJson().append()
        /*textList.forEach { (id, comp) ->
        }*/
        return json.toRawMessage()
    }

}

inline fun buildJsonUI(builder: BuilderJsonUI.() -> Unit = {}): String {
    return BuilderJsonUI().also(builder).build()
}