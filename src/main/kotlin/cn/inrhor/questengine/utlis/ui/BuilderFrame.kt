package cn.inrhor.questengine.utlis.ui


import cn.inrhor.questengine.api.ui.PartFrame
import cn.inrhor.questengine.script.kether.runEval
import cn.inrhor.questengine.utlis.copy
import cn.inrhor.questengine.utlis.toJsonStr
import org.bukkit.entity.Player
import taboolib.module.chat.TellrawJson

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
        noteComponent.forEach { (i ,v) ->
            if (!v.fork && textCondition(player, v.condition)) {
                val note = v.note
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
                            val rep = "$id;"
                            if (it.contains(rep)) {
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
    fun textCondition(player: Player?, conditions: String): Boolean {
        if (player == null) return true
        return runEval(player, conditions)
    }

    enum class Type {
        CUSTOM
    }

    fun addNote(partFrame: PartFrame) {
        noteComponent[partFrame.id] = NoteComponent(partFrame.note.toMutableList(), partFrame.condition)
    }

    fun copy(): BuilderFrame {
        val frame = BuilderFrame()
        noteComponent.forEach { (t, u) ->
            frame.noteComponent[t] = NoteComponent(u.note.copy(), u.condition, u.fork)
        }
        textComponent.forEach { (t, u) ->
            frame.textComponent[t] = u.copy()
        }
        return frame
    }

}

inline fun buildFrame(builder: BuilderFrame.() -> Unit = {}): BuilderFrame {
    return BuilderFrame().also(builder)
}