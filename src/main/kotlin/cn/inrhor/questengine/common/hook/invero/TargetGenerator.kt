package cn.inrhor.questengine.common.hook.invero

import cc.trixey.invero.core.Context
import cc.trixey.invero.core.geneartor.ContextGenerator

class TargetDoingGenerator: ContextGenerator() {

    override fun generate(context: Context) {
        generated = InvGenerator.targetGenerate(context, UiType.TARGET_DOING)
    }

}

class TargetCompleteGenerator: ContextGenerator() {

    override fun generate(context: Context) {
        generated = InvGenerator.targetGenerate(context, UiType.TARGET_COMPLETE)
    }

}