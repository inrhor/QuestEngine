package cn.inrhor.questengine.common.quest.target

object TargetAdyLeftItem/*: TargetExtend<AdyeshachEntityDamageEvent>()*/ {

//    override val name = "give ady-left item"

    /*init {
        if (Bukkit.getPluginManager().getPlugin("Adyeshach") != null) {
            event = AdyeshachEntityDamageEvent::class
            tasker {
                val questData = QuestManager.getDoingQuest(player) ?: return@tasker player
                if (!QuestManager.matchQuestMode(questData)) return@tasker player
                val innerData = questData.questInnerData
                val innerTarget = QuestManager.getDoingTarget(player, TargetNpcRightItem.name) ?: return@tasker player
                val id = object : ConditionType(mutableListOf("id")) {
                    override fun check(): Boolean {
                        return ClickNPC.idTrigger(innerTarget, entity.id)
                    }
                }
                val item = object : ConditionType("item") {
                    override fun check(): Boolean {
                        return ClickNPC.itemTrigger(player, questData, innerTarget, innerData)
                    }
                }
                TargetManager.set(TargetNpcRightItem.name, "id", id)
                TargetManager.set(TargetNpcRightItem.name, "item", item)
                player
            }
            TargetManager.register(TargetNpcRightItem.name, "id", mutableListOf("id"))
            TargetManager.register(TargetNpcRightItem.name, "item", "item")
        }
    }*/

}