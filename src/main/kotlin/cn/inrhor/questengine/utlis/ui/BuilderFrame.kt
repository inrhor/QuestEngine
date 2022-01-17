package cn.inrhor.questengine.utlis.ui

import cn.inrhor.questengine.script.kether.evalBoolean
import cn.inrhor.questengine.utlis.toJsonStr
import org.bukkit.entity.Player
import taboolib.module.chat.TellrawJson
import taboolib.module.configuration.Configuration

/**
 * 高度自定义 JSON 内容
 *
 * 框架容器
 */
class BuilderFrame {

    /**
     * 当前页面已打印的行数
     */
    var line = 0

    /**
     * 内容物组件
     */
    var noteComponent = mutableMapOf<String, NoteComponent>()

    /**
     * 文字组件
     */
    var textComponent = mutableMapOf<String, TextComponent>()

    /**
     * 页面内容
     */
    val pageFrame = mutableListOf<TellrawJson>()

    /**
     * 构建
     */
    fun build(player: Player? = null): MutableList<TellrawJson> {
        noteComponent.values.forEach { v ->
            if (!v.fork && textCondition(player, v.condition(player))) {
                val note = v.note(player)
                val json = autoPage(note.size)
                val sp = (note.toJsonStr()+"\n").split("@")
                var first = false
                sp.forEach {
                    if (!first) {
                        json.append(it)
                        first = true
                    }
                    textComponent.forEach { (id, comp) ->
                        if (textCondition(player, comp.condition)) {
                            var rep = "$id;"
                            if (it.contains(rep)) {
                                if (it.contains("-")) {
                                    val sort = it.split("-")[0]
                                    comp.autoCommand(sort)
                                    rep = "$sort-$id;"
                                } else {
                                    comp.autoCommand(id.split(".")[0])
                                }
                                json.append(comp.build(player))
                                json.append(it.replace(rep, ""))
                            }
                        }
                    }
                }
            }
        }
        return pageFrame
    }

    /**
     * 自动分页及分配 Json
     *
     * @param size 内容物占用的行数
     *
     * @return
     */
    private fun autoPage(size: Int): TellrawJson {
        line += size
        if (pageFrame.isEmpty() || needNewPage(pageFrame.size, pageFrame.isEmpty())) {
            line = 0
            pageFrame.add(TellrawJson())
        }
        return pageFrame[pageFrame.size-1]
    }

    /**
     * 是否需要新的页面
     *
     * @param size 内容物占用的行数
     * @param hasNote 当前页面是否已有内容
     *
     * @return
     */
    private fun needNewPage(size: Int, hasNote: Boolean = false): Boolean {
        if (size > 14 && hasNote) return true
        return line+size >14*(pageFrame.size)
    }

    /**
     * 组件所需条件
     */
    fun textCondition(player: Player?, conditions: List<String>): Boolean {
        if (player == null) return true
        return evalBoolean(player, conditions)
    }

    enum class Type {
        SORT, CUSTOM
    }

    fun yamlAddNote(yaml: Configuration, node: String, fork: Boolean = false) {
        noteComponent[node] = NoteComponent(
            yaml.getStringList(node).toMutableList(),
            yaml.getStringList("$node.condition").toMutableList(),
            fork)
    }

    /**
     * 遍历添加组件
     */
    fun sectionAdd(yaml: Configuration, path: String, type: Type) {
        yaml.getConfigurationSection(path)!!.getKeys(false).forEach { sign ->
            yamlAutoAdd(yaml, type, "$path.$sign", sign)
        }
    }

    /**
     * 自动分配组件
     */
    fun yamlAutoAdd(yaml: Configuration, type: Type, path: String, child: String = path) {
        yaml.getConfigurationSection(path)!!.getKeys(false).forEach { sign ->
            val node = "$path.$sign"
            when (sign) {
                "note" -> {
                    yamlAddNote(yaml, node)
                }
                "fork" -> {
                    yamlAddNote(yaml, node, true)
                }
                "condition" -> {}
                else -> {
                    val text = textComponent {
                        text = yaml.getStringList("$node.text").toMutableList()
                        hover = yaml.getStringList("$node.hover").toMutableList()
                        condition = yaml.getStringList("$node.condition").toMutableList()
                        command = if (type == Type.CUSTOM) {
                            yaml.getString("$node.command")?: ""
                        }else "/qen handbook sort "
                        this.type = type
                    }
                    textComponent["$child.$sign"] = text
                }
            }
        }
    }

    fun copy(): BuilderFrame {
        return buildFrame() {
            this@BuilderFrame.noteComponent.forEach { (t, u) ->
                noteComponent[t] = u
            }
            this@BuilderFrame.textComponent.forEach { (t, u) ->
                textComponent[t] = u
            }
        }
    }

}

inline fun buildFrame(builder: BuilderFrame.() -> Unit = {}): BuilderFrame {
    return BuilderFrame().also(builder)
}