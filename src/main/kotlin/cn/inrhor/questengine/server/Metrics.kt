package cn.inrhor.questengine.server

import cn.inrhor.questengine.common.dialog.DialogManager
import cn.inrhor.questengine.common.quest.manager.QuestManager
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.function.pluginVersion
import taboolib.module.metrics.Metrics
import taboolib.module.metrics.charts.SingleLineChart

object Metrics {

    private val bStats by lazy {
        Metrics(12482, pluginVersion, Platform.BUKKIT)
    }

    @Awake(LifeCycle.ACTIVE)
    fun init() {
        bStats.let {
            it.addCustomChart(SingleLineChart("quest") {
                QuestManager.getQuestMap().size
            })
            it.addCustomChart(SingleLineChart("dialog") {
                DialogManager.getMap().size
            })
        }
    }

}