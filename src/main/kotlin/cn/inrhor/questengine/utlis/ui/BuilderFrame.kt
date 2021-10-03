package cn.inrhor.questengine.utlis.ui

import cn.inrhor.questengine.script.kether.evalBoolean
import cn.inrhor.questengine.utlis.toJsonStr
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.library.configuration.YamlConfiguration
import taboolib.module.chat.TellrawJson

/**
 * 高度自定义 JSON 内容
 *
 * 框架容器
 */
open class BuilderFrame {

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
    open fun build(player: Player? = null): MutableList<TellrawJson> {
        noteComponent.values.forEach { v ->
            if (!v.fork) {
                val note = v.note
                val json = autoPage(note.size)
                val sp = (note.toJsonStr()+"\n").split("@")
                info("sp $sp")
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
                                    comp.setCommand(Type.SORT, sort)
                                    rep = "$sort-$id;"
                                } else {
                                    comp.setCommand(Type.SORT, id.split(".")[0])
                                }
                                json.append(comp.build())
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
     * 文字组件所需条件
     */
    private fun textCondition(player: Player?, conditions: MutableList<String>): Boolean {
        if (player == null) return true
        return evalBoolean(player, conditions)
    }

    enum class Type {
        SORT, CUSTOM
    }

    fun yamlAddNote(yaml: YamlConfiguration, node: String, fork: Boolean = false) {
        noteComponent[node] = NoteComponent(yaml.getStringList(node), fork)
    }

    fun sectionAdd(yaml: YamlConfiguration, path: String, type: Type) {
        yaml.getConfigurationSection(path).getKeys(false).forEach { sort ->
            yamlAutoAdd(yaml, type, "$path.$sort", sort)
        }
    }

    fun yamlAutoAdd(yaml: YamlConfiguration, type: Type, path: String, child: String = path) {
        yaml.getConfigurationSection(path).getKeys(false).forEach { sign ->
            val node = "$path.$sign"
            when (sign) {
                "note" -> {
                    yamlAddNote(yaml, node)
                }
                "fork" -> {
                    yamlAddNote(yaml, node, true)
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
                    textComponent["$child.$sign"] = text
                }
            }
        }
    }

    fun copy(): BuilderFrame {
        return buildFrame() {
            noteComponent = this@BuilderFrame.noteComponent
            textComponent = this@BuilderFrame.textComponent
        }
    }

}

inline fun buildFrame(builder: BuilderFrame.() -> Unit = {}): BuilderFrame {
    return BuilderFrame().also(builder)
}