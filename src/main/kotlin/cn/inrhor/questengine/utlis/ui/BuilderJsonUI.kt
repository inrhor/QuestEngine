package cn.inrhor.questengine.utlis.ui

import cn.inrhor.questengine.utlis.toJsonStr
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
    val textComponentMap = mutableMapOf<String, TextComponent>()

    fun yamlAddDesc(yaml: YamlConfiguration, node: String) {
        yaml.getStringList(node).forEach {
            description.add(it)
        }
    }

    fun sectionAdd(yaml: YamlConfiguration, path: String, type: Type) {
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
                        command = if (type == Type.CUSTOM) {
                            yaml.getString("$node.command")
                        }else "/qen handbook sort "
                    }
                    textComponentMap[id] = text
                }
            }
        }
    }

    enum class Type {
        SORT, CUSTOM
    }

    open fun build(): String {
        val text = description.toJsonStr()
        val json = TellrawJson()

        val sp = text.split("@")
        sp.forEach {
            textComponentMap.forEach { (id, comp) ->
                if (it.contains(id)) {
                    var rep = id
                    if (it.contains("-")) {
                        val sort = it.split("-")[0]
                        comp.setCommand(Type.SORT, sort)
                        rep = "$sort-$id"
                    } else {
                        comp.setCommand(Type.SORT, id.split(".")[0])
                    }
                    json.append(comp.build())
                    json.append(it.replace(rep, ""))
                } else {
                    json.append(it)
                }
            }
        }

        return json.toRawMessage()
    }

}

inline fun buildJsonUI(builder: BuilderJsonUI.() -> Unit = {}): String {
    return BuilderJsonUI().also(builder).build()
}