package pl.lukasz.culer.fgcs.rules

import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.rules.base.GrammarPostProcessor

class GAPostProcessor : GrammarPostProcessor() {
    override fun applyOperators(grammarController: GrammarController): Boolean {
        return false    //not yet implemented
    }
}