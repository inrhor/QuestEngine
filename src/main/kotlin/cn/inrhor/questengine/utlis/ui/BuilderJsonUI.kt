package cn.inrhor.questengine.utlis.ui

import taboolib.library.configuration.YamlConfiguration

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
    val textList = mutableMapOf<String, TextComponent>()

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
                    yaml.getStringList(node).forEach { tag ->
                        val text = textComponent {
                            val nodeTag = "$node.$tag"
                            text = yaml.getStringList("$nodeTag.text")
                            hover = yaml.getStringList("$nodeTag.hover")
                            command = "/qen handbook sort $sort"
                        }
                        textList[id] = text
                    }
                }
            }
        }
    }

}

inline fun buildJsonUI(builder: BuilderJsonUI.() -> Unit = {}): BuilderJsonUI {
    return BuilderJsonUI().also(builder)
}