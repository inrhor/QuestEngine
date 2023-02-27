package cn.inrhor.questengine.common.hook.invero

import cc.trixey.invero.core.Context
import cc.trixey.invero.core.geneartor.ContextGenerator

class QuestAcceptGenerator: ContextGenerator() {

    override fun generate(context: Context) {
        generated = InvGenerator.questGenerate(context, UiType.ACCEPT)
    }

}

class QuestCompleteGenerator: ContextGenerator() {

    override fun generate(context: Context) {
        generated = InvGenerator.questGenerate(context, UiType.COMPLETE)
    }

}