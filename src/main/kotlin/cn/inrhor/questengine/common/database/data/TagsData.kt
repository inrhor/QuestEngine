package cn.inrhor.questengine.common.database.data

class TagsData(private val tags: MutableSet<String> = mutableSetOf()) {

    fun addTag(tag: String) {
        tags.add(tag)
    }

    fun removeTag(tag: String) {
        tags.remove(tag)
    }

    fun clear(tag: String) {
        tags.clear()
    }

    fun has(tag: String): Boolean {
        return tags.contains(tag)
    }

    fun list(): MutableSet<String> {
        return tags
    }

    fun getList(): MutableList<String> {
        val l = mutableListOf<String>()
        tags.forEach {
            l.add(it)
        }
        return l
    }

}