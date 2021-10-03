/*
package cn.inrhor.questengine.utlis.ui

import cn.inrhor.questengine.script.kether.evalBoolean
import cn.inrhor.questengine.utlis.toJsonStr
import org.bukkit.entity.Player
import taboolib.library.configuration.YamlConfiguration
import taboolib.module.chat.TellrawJson

*/
/**
 * 高度自定义 JSON 内容
 *
 * 窗口组件
 *//*

open class BuilderJsonUI {

    */
/**
     * 内容物
     *//*

    val description = mutableListOf<String>()

    */
/**
     * 复制性内容物组件
     *//*

    var forkComponent = mutableMapOf<String, MutableList<String>>()

    */
/**
     * 文字组件，使内容物调用指定组件
     *//*

    var textComponentMap = mutableMapOf<String, TextComponent>()

    var line = 0

    fun clear() {
        description.clear()
    }

    fun yamlAddDesc(yaml: YamlConfiguration, node: String) {
        yaml.getStringList(node).forEach {
            description.add(it)
        }
    }

    fun sectionAdd(yaml: YamlConfiguration, path: String, type: Type) {
        yaml.getConfigurationSection(path).getKeys(false).forEach { sort ->
            yamlAdd(yaml, type, "$path.$sort", sort)
        }
    }

    fun yamlAdd(yaml: YamlConfiguration, type: Type, path: String, child: String = path) {
        yaml.getConfigurationSection(path).getKeys(false).forEach { sign ->
            val node = "$path.$sign"
            when (sign) {
                "note" -> {
                    yaml.getStringList(node).forEach { n ->
                        description.add(n)
                    }
                }
                "fork" -> {
                    forkComponent[path] = yaml.getStringList(node)
                }
                else -> {
                    val text = textComponent {
                        text = yaml.getStringList("$node.text")
                        hover = yaml.getStringList("$node.hover")
                        condition = yaml.getStringList("$node.condition")
                        command = if (type == Type.CUSTOM) {
                            yaml.getString("$node.command")?: ""
                        }else "/qen handbook sort "
                    }
                    textComponentMap["$child.$sign"] = text
                }
            }
        }
    }

    enum class Type {
        SORT, CUSTOM
    }

    open fun build(player: Player? = null): MutableList<TellrawJson> {
        val jsonList = mutableListOf<TellrawJson>()

        val text = description.toJsonStr()

        val sp = text.split("@")
        var first = false
        sp.forEach {
            if (!first) {
                autoPage(jsonList, ).append(it)
                first = true
            }
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
                        autoPage(jsonList, ).append(comp.build())
                        json.append(it.replace(rep, ""))
                    }*/
/* else {
                        info("append $it")
                        json.append(it)
                    }*//*

                }
            }
        }

        return jsonList
    }

    private fun autoPage(jsonList: MutableList<TellrawJson>, size: Int): TellrawJson {
        val theLine = line
        line += size
        if (needNewPage(jsonList.size, theLine, size, jsonList.isEmpty())) {
            line = 0
            jsonList.add(TellrawJson())
        }
        return jsonList[jsonList.size]
    }

    private fun needNewPage(page: Int, line: Int, size: Int, hasNote: Boolean = false): Boolean {
        if (size > 14 && hasNote) return true
        return line+size >14*(page+1)
    }

    private fun textCondition(player: Player?, conditions: MutableList<String>): Boolean {
        if (player == null) return true
        return evalBoolean(player, conditions)
    }

    fun copy(): BuilderJsonUI {
        return buildJsonUI {
            this@BuilderJsonUI.description.forEach {
                description.add(it)
            }
            forkComponent = this@BuilderJsonUI.forkComponent
            textComponentMap = this@BuilderJsonUI.textComponentMap
        }
    }

}

inline fun buildJsonUI(builder: BuilderJsonUI.() -> Unit = {}): BuilderJsonUI {
    return BuilderJsonUI().also(builder)
}*/
