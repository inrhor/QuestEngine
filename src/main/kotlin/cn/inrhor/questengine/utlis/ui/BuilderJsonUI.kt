package cn.inrhor.questengine.utlis.ui

import cn.inrhor.questengine.script.kether.evalBoolean
import cn.inrhor.questengine.utlis.toJsonStr
import org.bukkit.entity.Player
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
            yamlAdd(yaml, "$path.$sort", type)
        }
    }

    fun yamlAdd(yaml: YamlConfiguration, path: String, type: Type) {
        yaml.getConfigurationSection(path).getKeys(false).forEach { sign ->
            val node = "$path.$sign"
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
                textComponentMap[path] = text
            }
        }
    }

    enum class Type {
        SORT, CUSTOM
    }

    open fun build(player: Player? = null): TellrawJson {
        val text = description.toJsonStr()
        val json = TellrawJson()

        val sp = text.split("@")
        sp.forEach {
            textComponentMap.forEach { (id, comp) ->
                if (textCondition(player, comp.condition)) {
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
        }

        return json
    }

    private fun textCondition(player: Player?, conditions: MutableList<String>): Boolean {
        if (player == null) return true
        return evalBoolean(player, conditions)
    }

}

inline fun buildJsonUI(builder: BuilderJsonUI.() -> Unit = {}): BuilderJsonUI {
    return BuilderJsonUI().also(builder)
}